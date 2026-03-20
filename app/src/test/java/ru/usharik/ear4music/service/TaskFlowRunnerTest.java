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
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.NotesEnum;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TaskFlowRunner}.
 *
 * <ul>
 *   <li>Single-note flow uses {@code Schedulers.trampoline()} → fully synchronous.</li>
 *   <li>Sequence flow uses {@code Schedulers.io()} for subscribeOn (blockingFirst requires
 *       its own thread) and {@code Schedulers.trampoline()} for observeOn; completion is
 *       awaited via {@link CountDownLatch}.</li>
 * </ul>
 */
class TaskFlowRunnerTest {

    // --- Test doubles -----------------------------------------------------------

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

    // --- Fixtures ---------------------------------------------------------------

    private static final List<NotesEnum> MELODY = Arrays.asList(NotesEnum.C, NotesEnum.E, NotesEnum.G);
    // 1 ms longitude → notes finish immediately, no real waits
    private static final int FAST_LONGITUDE = 1;

    private RecordingMidiPlayer midi;
    private RecordingNoteEventListener ui;
    private StatisticsStorage stats;
    private PublishSubject<NoteInfo> keyboard;

    @BeforeEach
    void setUp() {
        midi = new RecordingMidiPlayer();
        ui = new RecordingNoteEventListener();
        stats = new StatisticsStorage();
        keyboard = PublishSubject.create();
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
                Schedulers.trampoline(), Schedulers.trampoline());
    }

    @Test
    @DisplayName("Single-note: plays every note via MidiPlayer")
    void singleNote_playsAllNotes() {
        singleNoteRunner().buildSingleNoteFlow(singleNoteSource(), () -> {}, () -> {});
        assertEquals(MELODY, midi.played);
    }

    @Test
    @DisplayName("Single-note: playNoteWithScale used when isPlayWithScale=true")
    void singleNote_usesPlayWithScaleWhenFlagSet() {
        Observable<NoteInfo[]> source =
                new NoteSequenceEmitter(60_000 / FAST_LONGITUDE, 1, 1, true, true)
                        .createObservable(List.of(NotesEnum.C));

        singleNoteRunner().buildSingleNoteFlow(source, () -> {}, () -> {});

        assertTrue(midi.playedWithScale.contains(NotesEnum.C));
        assertTrue(midi.played.isEmpty());
    }

    @Test
    @DisplayName("Single-note: onNoteActive called once per note")
    void singleNote_onNoteActiveCalledForEachNote() {
        singleNoteRunner().buildSingleNoteFlow(singleNoteSource(), () -> {}, () -> {});
        assertEquals(MELODY.size(), ui.active.size());
    }

    @Test
    @DisplayName("Single-note: onProgressUpdated called once per note")
    void singleNote_onProgressUpdatedCalledForEachNote() {
        singleNoteRunner().buildSingleNoteFlow(singleNoteSource(), () -> {}, () -> {});
        assertEquals(MELODY.size(), ui.progress.size());
    }

    @Test
    @DisplayName("Single-note: statistics records every note")
    void singleNote_statisticsRecordsAllNotes() {
        singleNoteRunner().buildSingleNoteFlow(singleNoteSource(), () -> {}, () -> {});
        assertEquals(MELODY.size(), stats.getOverallCount());
    }

    @Test
    @DisplayName("Single-note: onComplete callback is invoked")
    void singleNote_onCompleteInvoked() {
        AtomicInteger completeCalls = new AtomicInteger(0);
        singleNoteRunner().buildSingleNoteFlow(singleNoteSource(), completeCalls::incrementAndGet, () -> {});
        assertEquals(1, completeCalls.get());
    }

    @Test
    @DisplayName("Single-note: onStop callback invoked when disposed before completion")
    void singleNote_onStopInvokedOnDispose() throws InterruptedException {
        AtomicInteger stopCalls = new AtomicInteger(0);
        // infinite source so we can dispose before it completes
        Observable<NoteInfo[]> infinite = Observable.create(e -> {
            long num = 0;
            while (!e.isDisposed()) {
                e.onNext(new NoteInfo[]{new NoteInfo(num++, NotesEnum.C, null, false, false, 1)});
            }
        });
        Disposable d = new TaskFlowRunner(midi, ui, stats, keyboard,
                Schedulers.io(), Schedulers.trampoline())
                .buildSingleNoteFlow(infinite, () -> {}, stopCalls::incrementAndGet);
        Thread.sleep(20);
        d.dispose();
        Thread.sleep(20);
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
        // subscribeOn Schedulers.io() — required: blockingFirst must run on a background thread.
        return new TaskFlowRunner(midi, ui, stats, keyboard,
                Schedulers.io(), Schedulers.trampoline());
    }

    private void awaitCompletion(CountDownLatch latch) throws InterruptedException {
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Sequence flow did not complete in time");
    }

    @Test
    @DisplayName("Sequence: plays all notes in every group via MidiPlayer")
    void sequence_playsAllNotesInAllGroups() throws InterruptedException {
        // 2 groups × 2 notes = 4 play calls
        CountDownLatch done = new CountDownLatch(1);
        sequenceRunner().buildSequenceFlow(sequenceSource(2, 2), done::countDown, () -> {});
        awaitCompletion(done);
        assertEquals(4, midi.played.size());
    }

    @Test
    @DisplayName("Sequence: onSequenceGroupStarted called once per group")
    void sequence_groupStartedCalledPerGroup() throws InterruptedException {
        CountDownLatch done = new CountDownLatch(1);
        sequenceRunner().buildSequenceFlow(sequenceSource(2, 2), done::countDown, () -> {});
        awaitCompletion(done);
        assertEquals(2, ui.groupsStarted.get());
    }

    @Test
    @DisplayName("Sequence: onSequenceNoteActive called once per individual note")
    void sequence_sequenceNoteActiveCalledPerNote() throws InterruptedException {
        // 1 group × 4 notes (notesInSequence=4, sequencesInSubTask=1)
        CountDownLatch done = new CountDownLatch(1);
        sequenceRunner().buildSequenceFlow(sequenceSource(4, 1), done::countDown, () -> {});
        awaitCompletion(done);
        assertEquals(4, ui.sequenceActive.size());
    }

    @Test
    @DisplayName("Sequence: onProgressUpdated called once per individual note")
    void sequence_progressUpdatedCalledPerNote() throws InterruptedException {
        CountDownLatch done = new CountDownLatch(1);
        sequenceRunner().buildSequenceFlow(sequenceSource(2, 2), done::countDown, () -> {});
        awaitCompletion(done);
        assertEquals(4, ui.progress.size());
    }

    @Test
    @DisplayName("Sequence: when no keyboard press, note counts as missed in statistics")
    void sequence_noKeyboardPress_noteCountedAsMissed() throws InterruptedException {
        // 1-note group, no keyboard emission → timeout fires with original note → pressedNote==null
        CountDownLatch done = new CountDownLatch(1);
        sequenceRunner().buildSequenceFlow(sequenceSource(1, 1), done::countDown, () -> {});
        awaitCompletion(done);
        assertEquals(1, stats.getMissedCount());
        assertEquals(0, stats.getCorrectCount());
    }

    @Test
    @DisplayName("Sequence: keyboard press with correct note is counted as correct")
    void sequence_correctKeyboardPress_countedAsCorrect() throws InterruptedException {
        // Source emits a single 1-note group with note C and longitude 500ms
        List<NotesEnum> melody = List.of(NotesEnum.C);
        Observable<NoteInfo[]> source =
                new NoteSequenceEmitter(120, 1, 1, false, true).createObservable(melody);

        CountDownLatch done = new CountDownLatch(1);
        new TaskFlowRunner(midi, ui, stats, keyboard,
                Schedulers.io(), Schedulers.trampoline())
                .buildSequenceFlow(source, done::countDown, () -> {});

        // Emit a correct keyboard press shortly after the note starts
        Thread.sleep(30);
        NoteInfo correctPress = new NoteInfo(0, NotesEnum.C, NotesEnum.C, false, false, 0);
        keyboard.onNext(correctPress);

        awaitCompletion(done);
        assertEquals(1, stats.getCorrectCount());
        assertEquals(0, stats.getMissedCount());
    }

    @Test
    @DisplayName("Sequence: onComplete callback is invoked after all notes")
    void sequence_onCompleteInvoked() throws InterruptedException {
        AtomicInteger completeCalls = new AtomicInteger(0);
        CountDownLatch done = new CountDownLatch(1);
        sequenceRunner().buildSequenceFlow(sequenceSource(1, 2), () -> {
            completeCalls.incrementAndGet();
            done.countDown();
        }, () -> {});
        awaitCompletion(done);
        assertEquals(1, completeCalls.get());
    }
}

