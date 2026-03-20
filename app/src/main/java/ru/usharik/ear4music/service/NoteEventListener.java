package ru.usharik.ear4music.service;

import ru.usharik.ear4music.NoteInfo;

import java.util.function.Consumer;

public interface NoteEventListener {

    /**
     * Sequence mode: a new note group is about to start playing.
     * Implementations typically clear the currently highlighted key.
     */
    void onSequenceGroupStarted();

    /**
     * Sequence mode: one note of a group is now active and the user is expected to press it.
     */
    void onSequenceNoteActive(NoteInfo noteInfo);

    /**
     * Called after every processed note (both modes) with the note that was scored
     * (may be the original note when the user missed or answered incorrectly in time).
     */
    void onProgressUpdated(NoteInfo noteInfo);

    static NoteEventListener create(
            Consumer<NoteInfo> onNoteActive,
            Runnable onSequenceGroupStarted,
            Consumer<NoteInfo> onSequenceNoteActive,
            Consumer<NoteInfo> onProgressUpdated
    ) {
        return new NoteEventListener() {

            @Override
            public void onSequenceGroupStarted() {
                onSequenceGroupStarted.run();
            }

            @Override
            public void onSequenceNoteActive(NoteInfo noteInfo) {
                onSequenceNoteActive.accept(noteInfo);
            }

            @Override
            public void onProgressUpdated(NoteInfo noteInfo) {
                onProgressUpdated.accept(noteInfo);
            }
        };
    }
}

