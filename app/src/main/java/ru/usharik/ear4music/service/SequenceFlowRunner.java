package ru.usharik.ear4music.service;

import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.widget.KeyPress;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;

public class SequenceFlowRunner {

    private final MidiPlayer midiPlayer;
    private final Runnable onSequenceGroupStarted;
    private final Consumer<NoteInfo> onSequenceNoteActive;
    private final Consumer<NoteInfo> onProgressUpdated;
    private final StatisticsStorage statisticsStorage;
    private final Observable<KeyPress> keyboardObservable;
    private final Scheduler subscribeOnScheduler;
    private final Scheduler observeOnScheduler;

    public SequenceFlowRunner(MidiPlayer midiPlayer,
                              Runnable onSequenceGroupStarted,
                              Consumer<NoteInfo> onSequenceNoteActive,
                              Consumer<NoteInfo> onProgressUpdated,
                              StatisticsStorage statisticsStorage,
                              Observable<KeyPress> keyboardObservable,
                              Scheduler subscribeOnScheduler,
                              Scheduler observeOnScheduler) {
        this.midiPlayer = midiPlayer;
        this.onSequenceGroupStarted = onSequenceGroupStarted;
        this.onSequenceNoteActive = onSequenceNoteActive;
        this.onProgressUpdated = onProgressUpdated;
        this.statisticsStorage = statisticsStorage;
        this.keyboardObservable = keyboardObservable;
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
    }

    public Disposable buildFlow(Observable<NoteInfo[]> source,
                                Runnable onComplete,
                                Runnable onStop) {
        return source
                .subscribeOn(subscribeOnScheduler)
                .flatMap(notes -> {
                    onSequenceGroupStarted.run();
                    for (NoteInfo nt : notes) {
                        midiPlayer.playNote(nt.note, nt.longitude);
                    }
                    return Observable.just(notes);
                })
                .flatMap(Observable::fromArray)
                .doOnNext(noteInfo -> {
                    onSequenceNoteActive.accept(noteInfo);
                    // Wait for the user's key press, or treat as missed after timeout.
                    KeyPress keyPress = keyboardObservable
                            .timeout(noteInfo.longitude,
                                    TimeUnit.MILLISECONDS,
                                    Observable.just(KeyPress.missed()))
                            .blockingFirst();
                    // Join prompt (noteInfo) with the user's raw input (keyPress) to form an answer.
                    statisticsStorage.submitAnswer(
                            noteInfo.num, noteInfo.note, keyPress.pressedNote(), noteInfo.time);
                    onProgressUpdated.accept(noteInfo);
                })
                .observeOn(observeOnScheduler)
                .doOnComplete(onComplete::run)
                .doOnDispose(onStop::run)
                .subscribe();
    }
}
