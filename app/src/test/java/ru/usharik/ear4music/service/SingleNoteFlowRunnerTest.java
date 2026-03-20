package ru.usharik.ear4music.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.NotesEnum;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SingleNoteFlowRunner}.
 *
 * <p>All scheduling is driven by a single {@link TestScheduler} — no real threads,
 * no {@code Thread.sleep()}, no {@code CountDownLatch}.
 * {@code testScheduler.triggerActions()} runs the subscription synchronously and fires
 * every operator in the chain.</p>
 */
class SingleNoteFlowRunnerTest {

    private static final List<NotesEnum> MELODY = List.of(NotesEnum.C, NotesEnum.E, NotesEnum.G);
    private static final int FAST_LONGITUDE = 1;

    private RecordingMidiPlayer midi;
    private RecordingNoteEventListener ui;
    private TestScheduler testScheduler;

    @BeforeEach
    void setUp() {
        midi = new RecordingMidiPlayer();
        ui = new RecordingNoteEventListener();
        testScheduler = new TestScheduler();
    }

    private Observable<NoteInfo[]> source() {
        return new NoteSequenceEmitter(60_000 / FAST_LONGITUDE, 1, MELODY.size(), false, true)
                .createObservable(MELODY);
    }

    private SingleNoteFlowRunner runner() {
        return new SingleNoteFlowRunner(
                midi,
                ui::onNoteActive,
                ui::onProgressUpdated,
                testScheduler,
                testScheduler);
    }

    @Test
    @DisplayName("Single-note: plays every note via MidiPlayer")
    void playsAllNotes() {
        runner().buildFlow(source(), () -> {}, () -> {});
        testScheduler.triggerActions();
        assertEquals(MELODY, midi.played);
    }

    @Test
    @DisplayName("Single-note: playNoteWithScale used when isPlayWithScale=true")
    void usesPlayWithScaleWhenFlagSet() {
        Observable<NoteInfo[]> src =
                new NoteSequenceEmitter(60_000 / FAST_LONGITUDE, 1, 1, true, true)
                        .createObservable(List.of(NotesEnum.C));

        runner().buildFlow(src, () -> {}, () -> {});
        testScheduler.triggerActions();

        assertTrue(midi.playedWithScale.contains(NotesEnum.C));
        assertTrue(midi.played.isEmpty());
    }

    @Test
    @DisplayName("Single-note: onNoteActive called once per note")
    void onNoteActiveCalledForEachNote() {
        runner().buildFlow(source(), () -> {}, () -> {});
        testScheduler.triggerActions();
        assertEquals(MELODY.size(), ui.active.size());
    }

    @Test
    @DisplayName("Single-note: onProgressUpdated called once per note")
    void onProgressUpdatedCalledForEachNote() {
        runner().buildFlow(source(), () -> {}, () -> {});
        testScheduler.triggerActions();
        assertEquals(MELODY.size(), ui.progress.size());
    }

    @Test
    @DisplayName("Single-note: onComplete callback is invoked")
    void onCompleteInvoked() {
        AtomicInteger completeCalls = new AtomicInteger(0);
        runner().buildFlow(source(), completeCalls::incrementAndGet, () -> {});
        testScheduler.triggerActions();
        assertEquals(1, completeCalls.get());
    }

    @Test
    @DisplayName("Single-note: onStop callback invoked when disposed before completion")
    void onStopInvokedOnDispose() {
        AtomicInteger stopCalls = new AtomicInteger(0);
        Observable<NoteInfo[]> infinite = Observable.interval(1, TimeUnit.MILLISECONDS, testScheduler)
                .map(n -> new NoteInfo[]{new NoteInfo(n, NotesEnum.C, null, false, false, 1)});
        Disposable d = runner().buildFlow(infinite, () -> {}, stopCalls::incrementAndGet);
        testScheduler.triggerActions();
        testScheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS);
        d.dispose();
        assertEquals(1, stopCalls.get());
    }

    // --- Test helpers -----------------------------------------------------------

    private static class RecordingMidiPlayer implements MidiPlayer {
        final List<NotesEnum> played = new ArrayList<>();
        final List<NotesEnum> playedWithScale = new ArrayList<>();

        @Override public void playNote(NotesEnum note, int longitude) { played.add(note); }
        @Override public void playNoteWithScale(NotesEnum note, int longitude) { playedWithScale.add(note); }
    }

    private static class RecordingNoteEventListener {
        final List<NoteInfo> active = new ArrayList<>();
        final List<NoteInfo> progress = new ArrayList<>();

        public void onNoteActive(NoteInfo noteInfo) { active.add(noteInfo); }
        public void onProgressUpdated(NoteInfo noteInfo) { progress.add(noteInfo); }
    }
}
