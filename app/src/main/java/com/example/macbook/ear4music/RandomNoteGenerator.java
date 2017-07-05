package com.example.macbook.ear4music;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by macbook on 05.07.17.
 */
public class RandomNoteGenerator {
    private Random rnd;
    private List<Integer> twoLastNotes;
    private List<NotesEnum> melody;

    public RandomNoteGenerator(List<NotesEnum> melody) {
        this.rnd = new Random();
        this.twoLastNotes = new ArrayList<>();
        this.melody = new ArrayList<>(melody);
    }

    public NotesEnum nextNote() {
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
}
