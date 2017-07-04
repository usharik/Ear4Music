package com.example.macbook.androidapp.listner;

import com.example.macbook.androidapp.NotesEnum;
import com.example.macbook.androidapp.R;
import com.example.macbook.androidapp.widget.PianoKeyboard;

/**
 * Created by macbook on 04.07.17.
 */
public interface MidiSupportListner {
    void onNewNote(NotesEnum currentNote);
    void onMissedAnswer(int noteNumber);
}
