package ru.usharik.ear4music.service;

import ru.usharik.ear4music.NoteInfo;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Builds the two RxJava subscription chains that drive task execution.
 *
 * <h2>Flow 1 — single-note mode ({@code notesInSequence == 1})</h2>
 * <pre>
 *   source → subscribeOn → doOnNext(play + ui + stats) → observeOn → doOnComplete/Dispose
 * </pre>
 * A parallel subscription on the keyboard observable also feeds {@link StatisticsStorage}.
 *
 * <h2>Flow 2 — sequence mode ({@code notesInSequence > 1})</h2>
 * <pre>
 *   source → subscribeOn → concatMap(play group → concatMap(await keyboard / timeout)) →
 *   observeOn → doOnComplete/Dispose
 * </pre>
 *
 * <p>No Android dependencies: schedulers and all side-effect callbacks are injected,
 * making the class fully testable on the JVM.</p>
 */
public class TaskFlowRunner {

    private final MidiPlayer midiPlayer;
    private final NoteEventListener noteEventListener;
    private final StatisticsStorage statisticsStorage;
    private final Observable<NoteInfo> keyboardObservable;
    private final Scheduler subscribeOnScheduler;
    private final Scheduler observeOnScheduler;
    private final Scheduler timeoutScheduler;

    public TaskFlowRunner(MidiPlayer midiPlayer,
                          NoteEventListener noteEventListener,
                          StatisticsStorage statisticsStorage,
                          Observable<NoteInfo> keyboardObservable,
                          Scheduler subscribeOnScheduler,
                          Scheduler observeOnScheduler,
                          Scheduler timeoutScheduler) {
        this.midiPlayer = midiPlayer;
        this.noteEventListener = noteEventListener;
        this.statisticsStorage = statisticsStorage;
        this.keyboardObservable = keyboardObservable;
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
        this.timeoutScheduler = timeoutScheduler;
    }

    /**
     * Builds the single-note-mode subscription chain and returns the {@link Disposable}.
     *
     * <p>Callers must also subscribe {@code keyboardObservable} to
     * {@code statisticsStorage::submitAnswer} separately and add that disposable to the
     * same {@code CompositeDisposable}.</p>
     *
     * @param source     upstream Observable from {@link NoteSequenceEmitter}
     * @param onComplete called on the {@code observeOnScheduler} when all notes finish
     * @param onStop     called on the {@code observeOnScheduler} when the chain is disposed
     */
    public Disposable buildSingleNoteFlow(Observable<NoteInfo[]> source,
                                          Runnable onComplete,
                                          Runnable onStop) {
        return source
                .subscribeOn(subscribeOnScheduler)
                .doOnNext(notes -> {
                    noteEventListener.onNoteActive(notes[0]);
                    if (notes[0].isPlayWithScale) {
                        midiPlayer.playNoteWithScale(notes[0].note, notes[0].longitude);
                    } else {
                        midiPlayer.playNote(notes[0].note, notes[0].longitude);
                    }
                    statisticsStorage.submitAnswer(notes[0]);
                    noteEventListener.onProgressUpdated(notes[0]);
                })
                .observeOn(observeOnScheduler)
                .doOnComplete(onComplete::run)
                .doOnDispose(onStop::run)
                .subscribe();
    }

    /**
     * Builds the sequence-mode subscription chain and returns the {@link Disposable}.
     *
     * <p>The chain is fully reactive: each note waits for a keyboard press or timeout
     * via {@code concatMap}, so no thread is ever blocked. The {@code timeoutScheduler}
     * controls the virtual clock used by the {@code timeout} operator, making this
     * chain trivially testable with {@link io.reactivex.rxjava3.schedulers.TestScheduler}.</p>
     *
     * @param source     upstream Observable from {@link NoteSequenceEmitter}
     * @param onComplete called on the {@code observeOnScheduler} when all notes finish
     * @param onStop     called on the {@code observeOnScheduler} when the chain is disposed
     */
    public Disposable buildSequenceFlow(Observable<NoteInfo[]> source,
                                        Runnable onComplete,
                                        Runnable onStop) {
        return source
                .subscribeOn(subscribeOnScheduler)
                .concatMap(notes -> {
                    noteEventListener.onSequenceGroupStarted();
                    for (NoteInfo nt : notes) {
                        midiPlayer.playNote(nt.note, nt.longitude);
                    }
                    return Observable.fromArray(notes)
                            .concatMap(noteInfo -> {
                                noteEventListener.onSequenceNoteActive(noteInfo);
                                return keyboardObservable
                                        .timeout(noteInfo.longitude, TimeUnit.MILLISECONDS,
                                                timeoutScheduler, Observable.just(noteInfo))
                                        .take(1)
                                        .doOnNext(pressed -> {
                                            statisticsStorage.submitAnswer(pressed);
                                            noteEventListener.onProgressUpdated(pressed);
                                        });
                            });
                })
                .observeOn(observeOnScheduler)
                .doOnComplete(onComplete::run)
                .doOnDispose(onStop::run)
                .subscribe();
    }
}

