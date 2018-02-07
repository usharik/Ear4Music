package com.example.macbook.ear4music.service;

import com.example.macbook.ear4music.NotesEnum;

import java.util.List;

/**
 * Created by au185034 on 20/02/2018.
 */

public class MelodyNoteGenerator implements NoteGenerator {

    private final List<NotesEnum> melody;
    private int index;

    public MelodyNoteGenerator(List<NotesEnum> melody) {
        this.melody = melody;
        this.index = 0;
    }

    @Override
    public NotesEnum nextNote() {
        if (index == melody.size()) {
            throw new RuntimeException("Nothing to generate");
        }
        return melody.get(index++);
    }

    @Override
    public boolean hasNextNote() {
        return index < melody.size();
    }
}
