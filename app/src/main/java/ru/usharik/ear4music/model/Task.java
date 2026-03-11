package ru.usharik.ear4music.model;

import java.io.Serializable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by macbook on 05.02.18.
 */

@Entity(tableName = "task")
public class Task implements Serializable {
    private static final long serialVersionUID = 123L;

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private int nameId;

    private int setOfNotesId;

    private int setOfNotesHighlightingId;

    private int donePercent;

    public Task() {
    }

    @Ignore
    public Task(Long id, int nameId, int setOfNotesId, int setOfNotesHighlightingId,
            int donePercent) {
        this.id = id;
        this.nameId = nameId;
        this.setOfNotesId = setOfNotesId;
        this.setOfNotesHighlightingId = setOfNotesHighlightingId;
        this.donePercent = donePercent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNameId() {
        return nameId;
    }

    public void setNameId(int nameId) {
        this.nameId = nameId;
    }

    public int getSetOfNotesId() {
        return setOfNotesId;
    }

    public void setSetOfNotesId(int setOfNotesId) {
        this.setOfNotesId = setOfNotesId;
    }

    public int getSetOfNotesHighlightingId() {
        return setOfNotesHighlightingId;
    }

    public void setSetOfNotesHighlightingId(int setOfNotesHighlightingId) {
        this.setOfNotesHighlightingId = setOfNotesHighlightingId;
    }

    public int getDonePercent() {
        return donePercent;
    }

    public void setDonePercent(int donePercent) {
        this.donePercent = donePercent;
    }
}
