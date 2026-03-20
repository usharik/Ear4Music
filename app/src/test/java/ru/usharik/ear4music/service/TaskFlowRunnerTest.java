package ru.usharik.ear4music.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import io.reactivex.rxjava3.subjects.PublishSubject;
import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.NotesEnum;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TaskFlowRunner}.
 *
 * <p>All scheduling is driven by a single {@link TestScheduler} — no real threads,
 * no {@code Thread.sleep()}, no {@code CountDownLatch}.
 *
 * <ul>
 *   <li>Single-note flow: {@code testScheduler.triggerActions()} runs the subscription
 *       synchronously and fires every operator in the chain.</li>
 *   <li>Sequence flow: {@code triggerActions()} starts the subscription and subscribes to
 *       the first timeout; {@code advanceTimeBy(N ms)} advances virtual time so that all
 *       per-note timeouts fire in sequence, including the final {@code doOnComplete}.</li>
 * </ul>
 */
class TaskFlowRunnerTest {

    private static final List<NotesEnum> MELODY = List.of(NotesEnum.C, NotesEnum.E, NotesEnum.G);
    // 1 ms longitude → timeouts fire after a single advanceTimeBy(1, MILLISECONDS)
    private static final int FAST_LONGITUDE = 1;

    private RecordingMidiPlayer midi;
    private RecordingNoteEventListener ui;
    private StatisticsStorage stats;
    private PublishSubject<NoteInfo> keyboard;
    private TestScheduler testScheduler;

    @BeforeEach
    void setUp() {
        midi = new RecordingMidiPlayer();
        ui = new RecordingNoteEventListener();
        stats = new StatisticsStorage();
        keyboard = PublishSubject.create();
        testScheduler = new TestScheduler();
    }

    // ============================================================================
    // Single-note flow
    // ============================================================================

    private Observable<NoteInfo[]> singleNoteSource() {
        // melody mode, notesInSequence=1, longitude=1ms
        return new NoteSequenceEmitter(60_000 / FAST_LONGITUDE, 1, MELODY.size(), false, true)
                .createObservable(MELODY);
    }

    private TaskFlowRunner singleNoteRunner() {
        return new TaskFlowRunner(midi, ui, stats, keyboard,
                testScheduler, testScheduler);
    }

    @Test
    @DisplayName("Single-note: plays every note via MidiPlayer")
    void singleNote_playsAllNotes() {
        singleNoteRunner().buildSingleNoteFlow(singleNoteSource(), () -> {}, () -> {});
        testScheduler.triggerActions();
        assertEquals(MELODY, midi.played);
    }

    @Test
    @DisplayName("Single-note: playNoteWithScale used when isPlayWithScale=true")
    void singleNote_usesPlayWithScaleWhenFlagSet() {
        Observable<NoteInfo[]> source =
                new NoteSequenceEmitter(60_000 / FAST_LONGITUDE, 1, 1, true, true)
                        .createObservable(List.of(NotesEnum.C));

        singleNoteRunner().buildSingleNoteFlow(source, () -> {}, () -> {});
        testScheduler.triggerActions();

        assertTrue(midi.playedWithScale.contains(NotesEnum.C));
        assertTrue(midi.played.isEmpty());
    }

    @Test
    @DisplayName("Single-note: onNoteActive called once per note")
    void singleNote_onNoteActiveCalledForEachNote() {
        singleNoteRunner().buildSingleNoteFlow(singleNoteSource(), () -> {}, () -> {});
        testScheduler.triggerActions();
        assertEquals(MELODY.size(), ui.active.size());
    }

    @Test
    @DisplayName("Single-note: onProgressUpdated called once per note")
    void singleNote_onProgressUpdatedCalledForEachNote() {
        singleNoteRunner().buildSingleNoteFlow(singleNoteSource(), () -> {}, () -> {});
        testScheduler.triggerActions();
        assertEquals(MELODY.size(), ui.progress.size());
    }

    @Test
    @DisplayName("Single-note: statistics records every note")
    void singleNote_statisticsRecordsAllNotes() {
        singleNoteRunner().buildSingleNoteFlow(singleNoteSource(), () -> {}, () -> {});
        testScheduler.triggerActions();
        assertEquals(MELODY.size(), stats.getOverallCount());
    }

    @Test
    @DisplayName("Single-note: onComplete callback is invoked")
    void singleNote_onCompleteInvoked() {
        AtomicInteger completeCalls = new AtomicInteger(0);
        singleNoteRunner().buildSingleNoteFlow(singleNoteSource(), completeCalls::incrementAndGet, () -> {});
        testScheduler.triggerActions();
        assertEquals(1, completeCalls.get());
    }

    @Test
    @DisplayName("Single-note: onStop callback invoked when disposed before completion")
    void singleNote_onStopInvokedOnDispose() {
        AtomicInteger stopCalls = new AtomicInteger(0);
        // Infinite source via interval — controlled entirely by the TestScheduler
        Observable<NoteInfo[]> infinite = Observable.interval(1, TimeUnit.MILLISECONDS, testScheduler)
                .map(n -> new NoteInfo[]{new NoteInfo(n, NotesEnum.C, null, false, false, 1)});
        Disposable d = new TaskFlowRunner(midi, ui, stats, keyboard,
                testScheduler, testScheduler)
                .buildSingleNoteFlow(infinite, () -> {}, stopCalls::incrementAndGet);
        testScheduler.triggerActions();                          // start subscription
        testScheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS); // emit a few items
        d.dispose();                                            // doOnDispose fires synchronously
        assertEquals(1, stopCalls.get());
    }

    // ============================================================================
    // Sequence flow
    // ============================================================================

    /**
     * Source for sequence-flow tests using <em>random mode</em>
     * ({@code withNoteHighlighting=false}) so that {@code sequencesInSubTask} actually
     * controls the number of emitted groups.
     * <p>
     * Melody has 4 notes to satisfy {@link RandomNoteGenerator}'s anti-repetition logic.
     */
    private Observable<NoteInfo[]> sequenceSource(int notesInSequence, int sequencesInSubTask) {
        List<NotesEnum> m4 = Arrays.asList(NotesEnum.C, NotesEnum.D, NotesEnum.E, NotesEnum.G);
        return new NoteSequenceEmitter(
                60_000 / FAST_LONGITUDE, notesInSequence, sequencesInSubTask, false, false)
                .createObservable(m4);
    }

    private TaskFlowRunner sequenceRunner() {
        return new TaskFlowRunner(midi, ui, stats, keyboard,
                testScheduler, testScheduler);
    }

    /**
     * Advances virtual time so that every per-note timeout fires.
     *
     * @param totalNotes total number of individual notes across all groups
     */
    private void advanceThrough(int totalNotes) {
        testScheduler.advanceTimeBy((long) totalNotes * FAST_LONGITUDE, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("Sequence: plays all notes in every group via MidiPlayer")
    void sequence_playsAllNotesInAllGroups() {
        // 2 groups × 2 notes = 4 play calls
        sequenceRunner().buildSequenceFlow(sequenceSource(2, 2), () -> {}, () -> {});
        testScheduler.triggerActions(); // start subscription
        advanceThrough(4);             // 2 groups × 2 notes × 1 ms
        assertEquals(4, midi.played.size());
    }

    @Test
    @DisplayName("Sequence: onSequenceGroupStarted called once per group")
    void sequence_groupStartedCalledPerGroup() {
        sequenceRunner().buildSequenceFlow(sequenceSource(2, 2), () -> {}, () -> {});
        testScheduler.triggerActions();
        advanceThrough(4);
        assertEquals(2, ui.groupsStarted.get());
    }

    @Test
    @DisplayName("Sequence: onSequenceNoteActive called once per individual note")
    void sequence_sequenceNoteActiveCalledPerNote() {
        // 1 group × 4 notes (notesInSequence=4, sequencesInSubTask=1)
        sequenceRunner().buildSequenceFlow(sequenceSource(4, 1), () -> {}, () -> {});
        testScheduler.triggerActions();
        advanceThrough(4);
        assertEquals(4, ui.sequenceActive.size());
    }

    @Test
    @DisplayName("Sequence: onProgressUpdated called once per individual note")
    void sequence_progressUpdatedCalledPerNote() {
        sequenceRunner().buildSequenceFlow(sequenceSource(2, 2), () -> {}, () -> {});
        testScheduler.triggerActions();
        advanceThrough(4);
        assertEquals(4, ui.progress.size());
    }

    @Test
    @DisplayName("Sequence: when no keyboard press, note counts as missed in statistics")
    void sequence_noKeyboardPress_noteCountedAsMissed() {
        // 1-note group, no keyboard emission → timeout fires with original note → pressedNote==null
        sequenceRunner().buildSequenceFlow(sequenceSource(1, 1), () -> {}, () -> {});
        testScheduler.triggerActions();
        advanceThrough(1);
        assertEquals(1, stats.getMissedCount());
        assertEquals(0, stats.getCorrectCount());
    }

    @Test
    @DisplayName("Sequence: keyboard press with correct note is counted as correct")
    void sequence_correctKeyboardPress_countedAsCorrect() throws InterruptedException {
        // Source emits a single 1-note group (melody mode, 120 bpm → 500 ms longitude)
        List<NotesEnum> melody = List.of(NotesEnum.C);
        Observable<NoteInfo[]> source =
                new NoteSequenceEmitter(120, 1, 1, false, true).createObservable(melody);

        CountDownLatch flowCompleted = new CountDownLatch(1);

        // Start the flow in a separate thread since blockingFirst() will block
        new Thread(() -> {
            sequenceRunner().buildSequenceFlow(source, flowCompleted::countDown, () -> {});
            testScheduler.triggerActions();
        }).start();

        // Give flow time to start and reach blockingFirst()
        Thread.sleep(50);

        // Simulate keyboard press
        NoteInfo correctPress = new NoteInfo(0, NotesEnum.C, NotesEnum.C, false, false, 0);
        keyboard.onNext(correctPress);

        // Wait for flow to complete
        assertTrue(flowCompleted.await(1, TimeUnit.SECONDS), "Flow should complete");

        assertEquals(1, stats.getCorrectCount());
        assertEquals(0, stats.getMissedCount());
    }

    @Test
    @DisplayName("Sequence: keyboard press with wrong note is counted as incorrect")
    void sequence_wrongKeyboardPress_countedAsIncorrect() throws InterruptedException {
        List<NotesEnum> melody = List.of(NotesEnum.C);
        Observable<NoteInfo[]> source =
                new NoteSequenceEmitter(120, 1, 1, false, true).createObservable(melody);

        CountDownLatch flowCompleted = new CountDownLatch(1);

        // Start the flow in a separate thread since blockingFirst() will block
        new Thread(() -> {
            sequenceRunner().buildSequenceFlow(source, flowCompleted::countDown, () -> {});
            testScheduler.triggerActions();
        }).start();

        // Give flow time to start and reach blockingFirst()
        Thread.sleep(50);

        // Simulate keyboard press with wrong note
        NoteInfo wrongPress = new NoteInfo(0, NotesEnum.C, NotesEnum.D, false, false, 0);
        keyboard.onNext(wrongPress);

        // Wait for flow to complete
        assertTrue(flowCompleted.await(1, TimeUnit.SECONDS), "Flow should complete");

        assertEquals(0, stats.getCorrectCount());
        assertEquals(0, stats.getMissedCount());
        assertEquals(1, stats.getWrongCount());
    }

    @Test
    @DisplayName("Sequence: onComplete callback is invoked after all notes")
    void sequence_onCompleteInvoked() {
        AtomicInteger completeCalls = new AtomicInteger(0);
        sequenceRunner().buildSequenceFlow(sequenceSource(1, 2), completeCalls::incrementAndGet, () -> {});
        testScheduler.triggerActions(); // start subscription
        advanceThrough(2);              // 2 sequences × 1 note × 1 ms
        assertEquals(1, completeCalls.get());
    }

    // --- Test helpers -----------------------------------------------------------

    /** Captures every playNote / playNoteWithScale call. */
    private static class RecordingMidiPlayer implements MidiPlayer {
        final List<NotesEnum> played = new ArrayList<>();
        final List<NotesEnum> playedWithScale = new ArrayList<>();

        @Override
        public void playNote(NotesEnum note, int longitude) {
            played.add(note);
        }

        @Override
        public void playNoteWithScale(NotesEnum note, int longitude) {
            playedWithScale.add(note);
        }
    }

    /** Captures every callback invocation. */
    private static class RecordingNoteEventListener implements NoteEventListener {
        final List<NoteInfo> active = new ArrayList<>();
        final AtomicInteger groupsStarted = new AtomicInteger(0);
        final List<NoteInfo> sequenceActive = new ArrayList<>();
        final List<NoteInfo> progress = new ArrayList<>();

        @Override
        public void onNoteActive(NoteInfo noteInfo) {
            active.add(noteInfo);
        }

        @Override
        public void onSequenceGroupStarted() {
            groupsStarted.incrementAndGet();
        }

        @Override
        public void onSequenceNoteActive(NoteInfo noteInfo) {
            sequenceActive.add(noteInfo);
        }

        @Override
        public void onProgressUpdated(NoteInfo noteInfo) {
            progress.add(noteInfo);
        }
    }
}

