package ru.usharik.ear4music.listner;

import ru.usharik.ear4music.NoteInfo;

/**
 * Created by macbook on 04.07.17.
 */
public interface PianoKeyboardListener {
    void onNotePressed(NoteInfo pressedNote);
}
