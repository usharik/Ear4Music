package ru.usharik.ear4music.service;

import ru.usharik.ear4music.NoteInfo;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.function.Consumer;

public class SingleNoteFlowRunner {

    private final MidiPlayer midiPlayer;
    private final Consumer<NoteInfo> onNoteActive;
    private final Consumer<NoteInfo> onProgressUpdated;
    private final Scheduler subscribeOnScheduler;
    private final Scheduler observeOnScheduler;

    public SingleNoteFlowRunner(MidiPlayer midiPlayer,
                                Consumer<NoteInfo> onNoteActive,
                                Consumer<NoteInfo> onProgressUpdated,
                                Scheduler subscribeOnScheduler,
                                Scheduler observeOnScheduler) {
        this.midiPlayer = midiPlayer;
        this.onNoteActive = onNoteActive;
        this.onProgressUpdated = onProgressUpdated;
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
    }

    public Disposable buildFlow(Observable<NoteInfo[]> source,
                                Runnable onComplete,
                                Runnable onStop) {
        return source
                .subscribeOn(subscribeOnScheduler)
                .doOnNext(notes -> {
                    onNoteActive.accept(notes[0]);
                    if (notes[0].isPlayWithScale) {
                        midiPlayer.playNoteWithScale(notes[0].note, notes[0].longitude);
                    } else {
                        midiPlayer.playNote(notes[0].note, notes[0].longitude);
                    }
                    onProgressUpdated.accept(notes[0]);
                })
                .observeOn(observeOnScheduler)
                .doOnComplete(onComplete::run)
                .doOnDispose(onStop::run)
                .subscribe();
    }
}
