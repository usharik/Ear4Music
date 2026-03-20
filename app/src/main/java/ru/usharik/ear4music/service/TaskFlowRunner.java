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
 *   source → subscribeOn → flatMap(play group) → flatMap(expand) →
 *   doOnNext(wait keyboard / timeout) → observeOn → doOnComplete/Dispose
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

    public TaskFlowRunner(MidiPlayer midiPlayer,
                          NoteEventListener noteEventListener,
                          StatisticsStorage statisticsStorage,
                          Observable<NoteInfo> keyboardObservable,
                          Scheduler subscribeOnScheduler,
                          Scheduler observeOnScheduler) {
        this.midiPlayer = midiPlayer;
        this.noteEventListener = noteEventListener;
        this.statisticsStorage = statisticsStorage;
        this.keyboardObservable = keyboardObservable;
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
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
     * @param source     upstream Observable from {@link NoteSequenceEmitter}
     * @param onComplete called on the {@code observeOnScheduler} when all notes finish
     * @param onStop     called on the {@code observeOnScheduler} when the chain is disposed
     */
    public Disposable buildSequenceFlow(Observable<NoteInfo[]> source,
                                        Runnable onComplete,
                                        Runnable onStop) {
        return source
                .subscribeOn(subscribeOnScheduler)
                .flatMap(notes -> {
                    noteEventListener.onSequenceGroupStarted();
                    for (NoteInfo nt : notes) {
                        midiPlayer.playNote(nt.note, nt.longitude);
                    }
                    return Observable.just(notes);
                })
                .flatMap(Observable::fromArray)
                .doOnNext(noteInfo -> {
                    noteEventListener.onSequenceNoteActive(noteInfo);
                    NoteInfo pressed = keyboardObservable
                            .timeout(noteInfo.longitude, TimeUnit.MILLISECONDS,
                                    Observable.just(noteInfo))
                            .blockingFirst();
                    statisticsStorage.submitAnswer(pressed);
                    noteEventListener.onProgressUpdated(pressed);
                })
                .observeOn(observeOnScheduler)
                .doOnComplete(onComplete::run)
                .doOnDispose(onStop::run)
                .subscribe();
    }
}

