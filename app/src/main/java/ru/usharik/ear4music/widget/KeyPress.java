package ru.usharik.ear4music.widget;

import ru.usharik.ear4music.NotesEnum;

/**
 * Immutable value object representing a single key press event on the piano keyboard.
 * Contains only the pressed note and the timestamp of the press — no business logic.
 *
 * <p>{@code pressedNote} may be {@code null} when the user releases a touch that did not
 * land on any valid piano key (e.g., touched the gap between keys or an inactive area).
 */
public final class KeyPress {
    /** The note corresponding to the key that was pressed, or {@code null} if none matched. */
    private final NotesEnum pressedNote;
    private final long timestamp;

    public KeyPress(NotesEnum pressedNote, long timestamp) {
        this.pressedNote = pressedNote;
        this.timestamp = timestamp;
    }

    public NotesEnum pressedNote() {
        return pressedNote;
    }

    public long timestamp() {
        return timestamp;
    }
}
