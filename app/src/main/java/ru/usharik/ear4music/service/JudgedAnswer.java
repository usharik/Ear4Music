package ru.usharik.ear4music.service;

import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.NotesEnum;
import ru.usharik.ear4music.widget.KeyPress;

/**
 * Immutable domain object representing the evaluated result of one user answer.
 *
 * <p>A {@code JudgedAnswer} is created by combining a prompt ({@link NoteInfo}) with
 * the user's raw input ({@link KeyPress}).  It captures everything needed to record a
 * statistics entry without coupling callers to the internal {@link StatisticsStorage}
 * parameter list.</p>
 *
 * <p>Preferred factory methods (explicit activation time):
 * <ul>
 *   <li>{@link #from(NoteInfo, KeyPress, long)} — normal answer</li>
 *   <li>{@link #missed(NoteInfo, long)}          — missed answer (no key pressed in time)</li>
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

    /**
     * The epoch-millisecond timestamp at which the prompt became active for user input.
     * Used to compute reaction delay (= answer time − activationTimeMs).
     */
    public final long activationTimeMs;

    public JudgedAnswer(long noteNumber, NotesEnum expectedNote, NotesEnum pressedNote,
                        long activationTimeMs) {
        this.noteNumber = noteNumber;
        this.expectedNote = expectedNote;
        this.pressedNote = pressedNote;
        this.activationTimeMs = activationTimeMs;
    }

    /**
     * Creates a {@code JudgedAnswer} from a prompt, a user key-press, and the millisecond
     * timestamp at which the prompt was activated for input.
     *
     * @param prompt          the active prompt note
     * @param keyPress        the raw key-press event from the piano keyboard
     * @param activationTimeMs epoch-ms when the prompt became active for user input
     */
    public static JudgedAnswer from(NoteInfo prompt, KeyPress keyPress, long activationTimeMs) {
        return new JudgedAnswer(prompt.num, prompt.note, keyPress.pressedNote(), activationTimeMs);
    }

    /**
     * Creates a {@code JudgedAnswer} from a prompt and a user key-press, using the
     * {@link NoteInfo#time} construction timestamp as a proxy for activation time.
     *
     * @deprecated prefer {@link #from(NoteInfo, KeyPress, long)} to pass the actual
     *             prompt-activation timestamp for accurate reaction-delay measurement.
     */
    @Deprecated
    public static JudgedAnswer from(NoteInfo prompt, KeyPress keyPress) {
        return from(prompt, keyPress, prompt.time.getTime());
    }

    /**
     * Creates a {@code JudgedAnswer} representing a missed answer (no key was pressed),
     * using the given prompt-activation timestamp.
     *
     * @param prompt          the prompt note that was not answered in time
     * @param activationTimeMs epoch-ms when the prompt became active for user input
     */
    public static JudgedAnswer missed(NoteInfo prompt, long activationTimeMs) {
        return new JudgedAnswer(prompt.num, prompt.note, null, activationTimeMs);
    }

    /**
     * Creates a {@code JudgedAnswer} representing a missed answer, using the
     * {@link NoteInfo#time} construction timestamp as a proxy for activation time.
     *
     * @deprecated prefer {@link #missed(NoteInfo, long)} to pass the actual
     *             prompt-activation timestamp for accurate reaction-delay measurement.
     */
    @Deprecated
    public static JudgedAnswer missed(NoteInfo prompt) {
        return missed(prompt, prompt.time.getTime());
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
