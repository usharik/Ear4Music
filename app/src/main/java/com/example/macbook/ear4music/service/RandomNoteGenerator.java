package com.example.macbook.ear4music.service;

import com.example.macbook.ear4music.NotesEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by macbook on 05.07.17.
 */
public class RandomNoteGenerator implements NoteGenerator {
    private Random rnd;
    private List<Integer> twoLastNotes;
    private List<NotesEnum> melody;
    private int count;

    public RandomNoteGenerator(List<NotesEnum> melody, int count) {
        this.rnd = new Random();
        this.twoLastNotes = new ArrayList<>();
        this.melody = new ArrayList<>(melody);
        this.count = count;
    }

    public NotesEnum nextNote() {
        if (count == 0) {
            throw new RuntimeException("Nothing to generate");
        } else {
            count--;
        }
        int nt = rnd.nextInt(melody.size());
        if (twoLastNotes.size() != 2) {
            twoLastNotes.add(nt);
        } else {
            while (twoLastNotes.contains(nt)) nt = rnd.nextInt(melody.size());
            twoLastNotes.clear();
            twoLastNotes.add(nt);
        }
        return melody.get(nt);
    }

    public boolean hasNextNote() {
        return count != 0;
    }
}
