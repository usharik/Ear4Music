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
import io.reactivex.rxjava3.schedulers.TestScheduler;
import io.reactivex.rxjava3.subjects.PublishSubject;
import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.NotesEnum;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SequenceFlowRunner}.
 *
 * <p>Most tests use a {@link TestScheduler} to control virtual time so that per-note
 * timeouts fire without real waiting. The keyboard-press tests use a real thread because
 * {@code blockingFirst()} must be unblocked by an external emission.</p>
 */
class SequenceFlowRunnerTest {

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

    /**
     * Source for sequence-flow tests using <em>random mode</em>
     * ({@code withNoteHighlighting=false}) so that {@code sequencesInSubTask} controls the
     * number of emitted groups. Uses 4 notes to satisfy anti-repetition logic.
     */
    private Observable<NoteInfo[]> sequenceSource(int notesInSequence, int sequencesInSubTask) {
        List<NotesEnum> m4 = Arrays.asList(NotesEnum.C, NotesEnum.D, NotesEnum.E, NotesEnum.G);
        return new NoteSequenceEmitter(
                60_000 / FAST_LONGITUDE, notesInSequence, sequencesInSubTask, false, false)
                .createObservable(m4);
    }

    private SequenceFlowRunner runner() {
        return new SequenceFlowRunner(
                midi,
                ui::onSequenceGroupStarted,
                ui::onSequenceNoteActive,
                ui::onProgressUpdated,
                stats,
                keyboard,
                testScheduler,
                testScheduler
        );
    }

    private void advanceThrough(int totalNotes) {
        testScheduler.advanceTimeBy((long) totalNotes * FAST_LONGITUDE, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("Sequence: plays all notes in every group via MidiPlayer")
    void playsAllNotesInAllGroups() {
        runner().buildFlow(sequenceSource(2, 2), () -> {}, () -> {});
        testScheduler.triggerActions();
        advanceThrough(4);
        assertEquals(4, midi.played.size());
    }

    @Test
    @DisplayName("Sequence: onSequenceGroupStarted called once per group")
    void groupStartedCalledPerGroup() {
        runner().buildFlow(sequenceSource(2, 2), () -> {}, () -> {});
        testScheduler.triggerActions();
        advanceThrough(4);
        assertEquals(2, ui.groupsStarted.get());
    }

    @Test
    @DisplayName("Sequence: onSequenceNoteActive called once per individual note")
    void sequenceNoteActiveCalledPerNote() {
        runner().buildFlow(sequenceSource(4, 1), () -> {}, () -> {});
        testScheduler.triggerActions();
        advanceThrough(4);
        assertEquals(4, ui.sequenceActive.size());
    }

    @Test
    @DisplayName("Sequence: onProgressUpdated called once per individual note")
    void progressUpdatedCalledPerNote() {
        runner().buildFlow(sequenceSource(2, 2), () -> {}, () -> {});
        testScheduler.triggerActions();
        advanceThrough(4);
        assertEquals(4, ui.progress.size());
    }

    @Test
    @DisplayName("Sequence: when no keyboard press, note counts as missed in statistics")
    void noKeyboardPress_noteCountedAsMissed() {
        runner().buildFlow(sequenceSource(1, 1), () -> {}, () -> {});
        testScheduler.triggerActions();
        advanceThrough(1);
        assertEquals(1, stats.getMissedCount());
        assertEquals(0, stats.getCorrectCount());
    }

    @Test
    @DisplayName("Sequence: keyboard press with correct note is counted as correct")
    void correctKeyboardPress_countedAsCorrect() throws InterruptedException {
        List<NotesEnum> melody = List.of(NotesEnum.C);
        Observable<NoteInfo[]> source =
                new NoteSequenceEmitter(120, 1, 1, false, true).createObservable(melody);

        CountDownLatch flowCompleted = new CountDownLatch(1);
        new Thread(() -> {
            runner().buildFlow(source, flowCompleted::countDown, () -> {});
            testScheduler.triggerActions();
        }).start();

        Thread.sleep(50);
        keyboard.onNext(new NoteInfo(0, NotesEnum.C, NotesEnum.C, false, false, 0));
        assertTrue(flowCompleted.await(1, TimeUnit.SECONDS), "Flow should complete");

        assertEquals(1, stats.getCorrectCount());
        assertEquals(0, stats.getMissedCount());
    }

    @Test
    @DisplayName("Sequence: keyboard press with wrong note is counted as incorrect")
    void wrongKeyboardPress_countedAsIncorrect() throws InterruptedException {
        List<NotesEnum> melody = List.of(NotesEnum.C);
        Observable<NoteInfo[]> source =
                new NoteSequenceEmitter(120, 1, 1, false, true).createObservable(melody);

        CountDownLatch flowCompleted = new CountDownLatch(1);
        new Thread(() -> {
            runner().buildFlow(source, flowCompleted::countDown, () -> {});
            testScheduler.triggerActions();
        }).start();

        Thread.sleep(50);
        keyboard.onNext(new NoteInfo(0, NotesEnum.C, NotesEnum.D, false, false, 0));
        assertTrue(flowCompleted.await(1, TimeUnit.SECONDS), "Flow should complete");

        assertEquals(0, stats.getCorrectCount());
        assertEquals(0, stats.getMissedCount());
        assertEquals(1, stats.getWrongCount());
    }

    @Test
    @DisplayName("Sequence: onComplete callback is invoked after all notes")
    void onCompleteInvoked() {
        AtomicInteger completeCalls = new AtomicInteger(0);
        runner().buildFlow(sequenceSource(1, 2), completeCalls::incrementAndGet, () -> {});
        testScheduler.triggerActions();
        advanceThrough(2);
        assertEquals(1, completeCalls.get());
    }

    // --- Test helpers -----------------------------------------------------------

    private static class RecordingMidiPlayer implements MidiPlayer {
        final List<NotesEnum> played = new ArrayList<>();

        @Override public void playNote(NotesEnum note, int longitude) { played.add(note); }
        @Override public void playNoteWithScale(NotesEnum note, int longitude) {}
    }

    private static class RecordingNoteEventListener {
        final AtomicInteger groupsStarted = new AtomicInteger(0);
        final List<NoteInfo> sequenceActive = new ArrayList<>();
        final List<NoteInfo> progress = new ArrayList<>();

        public void onSequenceGroupStarted() { groupsStarted.incrementAndGet(); }
        public void onSequenceNoteActive(NoteInfo noteInfo) { sequenceActive.add(noteInfo); }
        public void onProgressUpdated(NoteInfo noteInfo) { progress.add(noteInfo); }
    }
}

