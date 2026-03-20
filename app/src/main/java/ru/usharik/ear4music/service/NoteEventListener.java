package ru.usharik.ear4music.service;

import ru.usharik.ear4music.NoteInfo;

/**
 * UI-side callbacks invoked by {@link TaskFlowRunner} as notes are processed.
 *
 * <p>All methods may be called from a background thread; implementations are responsible
 * for dispatching to the UI thread where needed.</p>
 */
public interface NoteEventListener {

    /**
     * Single-note mode: a note has been played and is now awaiting a keyboard response.
     * Implementations typically highlight the note on the piano keyboard.
     */
    void onNoteActive(NoteInfo noteInfo);

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
}

