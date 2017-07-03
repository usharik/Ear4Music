package com.example.macbook.androidapp;

import java.util.HashMap;

/**
 * Created by macbook on 02.07.17.
 */
public enum NotesEnum {
    C((byte) 0, "C"),
    D((byte) 2, "D"),
    E((byte) 4, "E"),
    F((byte) 5, "F"),
    G((byte) 7, "G"),
    A((byte) 9, "A"),
    B((byte) 11, "B"),
    C2((byte) 12, "C2");

    HashMap<String, NotesEnum> char2notes = fillChar2notes();

    private HashMap<String, NotesEnum> fillChar2notes() {
        HashMap<String, NotesEnum> map = new HashMap<>();
        map.put("C", C);
        map.put("D", D);
        map.put("E", E);
        map.put("F", F);
        map.put("G", G);
        map.put("A", A);
        map.put("B", B);
        map.put("C2", C2);
        return map;
    }

    static final byte C1 = 0x3C;
    byte pitch;
    String name;

    NotesEnum(byte pitch, String name) {
        this.pitch = (byte) (pitch + C1);
        this.name = name;
    }

    public byte getPitch() {
        return pitch;
    }

    public String getName() {
        return name;
    }

    public NotesEnum getNoteByString(String note) {
        return char2notes.get(note);
    }

    public byte getPitchByString(String note) {
        return char2notes.get(note).getPitch();
    }
}
