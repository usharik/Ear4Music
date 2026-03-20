package ru.usharik.ear4music.service;

import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.NotesEnum;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 * Pure Java class responsible for producing a cold RxJava 3 Observable that emits
 * {@link NoteInfo} arrays according to the current sub-task parameters.
 *
 * <p>Has no Android dependencies and is therefore directly unit-testable on the JVM.</p>
 */
public class NoteSequenceEmitter {

    private final int notesPerMinute;
    private final int notesInSequence;
    private final int sequencesInSubTask;
    private final boolean playWithScale;
    private final boolean withNoteHighlighting;

    public NoteSequenceEmitter(int notesPerMinute,
                               int notesInSequence,
                               int sequencesInSubTask,
                               boolean playWithScale,
                               boolean withNoteHighlighting) {
        this.notesPerMinute = notesPerMinute;
        this.notesInSequence = notesInSequence;
        this.sequencesInSubTask = sequencesInSubTask;
        this.playWithScale = playWithScale;
        this.withNoteHighlighting = withNoteHighlighting;
    }

    /**
     * Duration of a single note in milliseconds, derived from {@code notesPerMinute}.
     */
    public int getLongitude() {
        return (int) Math.round(60_000.0 / notesPerMinute);
    }

    /**
     * Creates a cold Observable that emits arrays of {@link NoteInfo}.
     *
     * <ul>
     *   <li>In <b>melody mode</b> ({@code withNoteHighlighting=true}) the notes follow the
     *       supplied {@code melody} in order via {@link MelodyNoteGenerator}.</li>
     *   <li>In <b>random mode</b> ({@code withNoteHighlighting=false}) notes are drawn
     *       randomly from {@code melody} via {@link RandomNoteGenerator}; total emissions
     *       equal {@code sequencesInSubTask}.</li>
     * </ul>
     *
     * <p>The Observable respects disposal: it stops emitting as soon as the downstream
     * is disposed and completes cleanly otherwise.</p>
     *
     * @param melody the set/sequence of notes to draw from
     * @return cold Observable of NoteInfo arrays; each array has {@code notesInSequence} elements
     */
    public Observable<NoteInfo[]> createObservable(final List<NotesEnum> melody) {
        final int longitude = getLongitude();

        return Observable.create(emitter -> {
            long noteNumber = 0;
            NoteGenerator noteGenerator = withNoteHighlighting
                    ? new MelodyNoteGenerator(melody)
                    : new RandomNoteGenerator(melody, sequencesInSubTask * notesInSequence);

            while (!emitter.isDisposed() && noteGenerator.hasNextNote()) {
                NoteInfo[] notes = new NoteInfo[notesInSequence];
                for (int i = 0; i < notesInSequence; i++) {
                    notes[i] = new NoteInfo(
                            noteNumber++,
                            noteGenerator.nextNote(),
                            null,
                            playWithScale,
                            withNoteHighlighting,
                            longitude);
                }
                emitter.onNext(notes);
            }

            if (!emitter.isDisposed()) {
                emitter.onComplete();
            }
        });
    }
}

