package com.example.macbook.ear4music.listner;

import com.example.macbook.ear4music.NotesEnum;

/**
 * Created by macbook on 04.07.17.
 */
public interface MidiSupportListner {
    void onNewNote(NotesEnum currentNote);
    void onMissedAnswer(int noteNumber);
}
