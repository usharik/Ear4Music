package com.example.macbook.ear4music.listner;

import com.example.macbook.ear4music.NoteInfo;

/**
 * Created by macbook on 04.07.17.
 */
public interface PianoKeyboardListener {
    void onNotePressed(NoteInfo pressedNote);
}
