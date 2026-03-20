package ru.usharik.ear4music.service;

import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.NotesEnum;
import ru.usharik.ear4music.widget.KeyPress;

import java.util.Date;

/**
 * Immutable domain object representing the evaluated result of one user answer.
 *
 * <p>A {@code JudgedAnswer} is created by combining a prompt ({@link NoteInfo}) with
 * the user's raw input ({@link KeyPress}).  It captures everything needed to record a
 * statistics entry without coupling callers to the internal {@link StatisticsStorage}
 * parameter list.</p>
 *
 * <p>Factory methods:
 * <ul>
 *   <li>{@link #from(NoteInfo, KeyPress)} — normal answer (key pressed by the user)</li>
 *   <li>{@link #missed(NoteInfo)}         — missed answer (no key pressed in time)</li>
 * </ul>
 * </p>
 */
public final class JudgedAnswer {

    /** Monotone identifier of the prompt note (maps to {@link NoteInfo#num}). */
    public final long noteNumber;

    /** The note the user was expected to identify. */
    public final NotesEnum expectedNote;

    /** The note actually pressed by the user, or {@code null} if no key was pressed. */
    public final NotesEnum pressedNote;

    /** The time at which the prompt was first presented (used to compute reaction delay). */
    public final Date noteTime;

    public JudgedAnswer(long noteNumber, NotesEnum expectedNote, NotesEnum pressedNote, Date noteTime) {
        this.noteNumber = noteNumber;
        this.expectedNote = expectedNote;
        this.pressedNote = pressedNote;
        this.noteTime = noteTime;
    }

    /**
     * Creates a {@code JudgedAnswer} from a prompt and a user key-press.
     *
     * @param prompt   the active prompt note at the time of the key press
     * @param keyPress the raw key-press event from the piano keyboard
     */
    public static JudgedAnswer from(NoteInfo prompt, KeyPress keyPress) {
        return new JudgedAnswer(prompt.num, prompt.note, keyPress.pressedNote(), prompt.time);
    }

    /**
     * Creates a {@code JudgedAnswer} representing a missed answer (no key was pressed).
     *
     * @param prompt the prompt note that was not answered in time
     */
    public static JudgedAnswer missed(NoteInfo prompt) {
        return new JudgedAnswer(prompt.num, prompt.note, null, prompt.time);
    }

    /** Returns {@code true} if the user pressed the correct note. */
    public boolean isCorrect() {
        return expectedNote != null && expectedNote == pressedNote;
    }

    /** Returns {@code true} if no note was pressed (timeout/missed). */
    public boolean isMissed() {
        return pressedNote == null;
    }
}
