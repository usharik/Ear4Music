package com.example.macbook.ear4music;

import java.util.*;

/**
 * Created by macbook on 02.07.17.
 */
public enum NotesEnum {
    C((byte)  0, false),
    Cs((byte) 1, true),
    D((byte)  2, false),
    Ds((byte) 3, true),
    E((byte)  4, false),
    F((byte)  5, false),
    Fs((byte) 6, true),
    G((byte)  7, false),
    Gs((byte) 8, true),
    A((byte)  9, false),
    As((byte) 10, true),
    B((byte)  11, false),
    C2((byte) 12, false);

    static final byte C1 = 0x3C;
    byte pitch;
    boolean sharp;
    static List<NotesEnum> whiteList;
    static List<NotesEnum> blackList;

    NotesEnum(byte pitch, boolean sharp) {
        this.pitch = (byte) (pitch + C1);
        this.sharp = sharp;
    }

    public byte getPitch() {
        return pitch;
    }

    public static List<NotesEnum> getWhite() {
        if (whiteList == null) {
            List<NotesEnum> list = new ArrayList<>();
            for (NotesEnum note : NotesEnum.values()) {
                if (!note.sharp) {
                    list.add(note);
                }
            }
            whiteList = Collections.unmodifiableList(list);
        }
        return whiteList;
    }

    public static List<NotesEnum> getBlack() {
        if (blackList == null) {
            List<NotesEnum> list = new ArrayList<>();
            for (NotesEnum note : NotesEnum.values()) {
                if (note.sharp) {
                    list.add(note);
                }
            }
            blackList = Collections.unmodifiableList(list);
        }
        return blackList;
    }
}
