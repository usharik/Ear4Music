package ru.usharik.ear4music.widget;

import ru.usharik.ear4music.NotesEnum;

/**
 * Immutable value object representing a single key press event on the piano keyboard.
 * Contains only the pressed note and the timestamp of the press — no business logic.
 *
 * <p>{@code pressedNote} may be {@code null} when the user releases a touch that did not
 * land on any valid piano key (e.g., touched the gap between keys or an inactive area).
 *
 * @param pressedNote The note corresponding to the key that was pressed, or {@code null} if none matched.
 */
public record KeyPress(NotesEnum pressedNote, long timestamp) {

    /**
     * Creates a {@code KeyPress} representing a missed answer (no key was pressed within the
     * allowed time). The {@link #pressedNote()} will be {@code null}.
     */
    public static KeyPress missed() {
        return new KeyPress(null, System.currentTimeMillis());
    }
}
