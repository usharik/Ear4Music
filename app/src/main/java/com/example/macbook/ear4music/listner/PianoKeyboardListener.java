package com.example.macbook.ear4music.listner;

import com.example.macbook.ear4music.RandomNotesTaskActivity;

/**
 * Created by macbook on 04.07.17.
 */
public interface PianoKeyboardListener {
    void onNotePressed(RandomNotesTaskActivity.NoteInfo pressedNote);
}
