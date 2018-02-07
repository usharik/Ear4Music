package com.example.macbook.ear4music;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by au185034 on 19/02/2018.
 */

public class NoteInfo {
    public long num;
    public NotesEnum note;
    public NotesEnum pressedNote;
    public boolean isPlayWithScale;
    public boolean isHighlighted;
    public int longitude;
    public Date time;

    public NoteInfo(final long num,
                    final NotesEnum note,
                    final NotesEnum pressedNote,
                    final boolean isPlayWithScale,
                    final boolean isHighlighted,
                    final int longitude) {
        this.num = num;
        this.note = note;
        this.pressedNote = pressedNote;
        this.isPlayWithScale = isPlayWithScale;
        this.isHighlighted = isHighlighted;
        this.longitude = longitude;
        this.time = Calendar.getInstance().getTime();
    }
}
