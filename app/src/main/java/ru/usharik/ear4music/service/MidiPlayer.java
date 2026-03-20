package ru.usharik.ear4music.service;

import ru.usharik.ear4music.NotesEnum;

/**
 * Abstraction over MIDI note playback.
 *
 * <p>Separates {@link TaskFlowRunner} from the concrete {@link MidiSupport} so that
 * the flow logic can be tested on the JVM without an Android runtime.</p>
 */
public interface MidiPlayer {

    /** Play a single note for {@code longitude} milliseconds. */
    void playNote(NotesEnum note, int longitude);

    /** Play a note preceded by its scale arpeggiation for {@code longitude} milliseconds. */
    void playNoteWithScale(NotesEnum note, int longitude);
}

