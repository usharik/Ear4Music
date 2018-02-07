package com.example.macbook.ear4music.service;

import com.example.macbook.ear4music.NotesEnum;

/**
 * Created by au185034 on 20/02/2018.
 */

public interface NoteGenerator {
    NotesEnum nextNote();
    boolean hasNextNote();
}
