package com.example.macbook.ear4music.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by macbook on 05.02.18.
 */

@Entity
public class Task implements Serializable {
    private static final long serialVersionUID = 123L;

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private int nameId;

    @NotNull
    private int setOfNotesId;

    @NotNull
    private int setOfNotesHighlightingId;

    @NotNull
    private int donePercent;

    @Generated(hash = 733837707)
    public Task() {
    }

    @Generated(hash = 299877085)
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

    public Integer getSetOfNotesId() {
        return setOfNotesId;
    }

    public void setSetOfNotesId(Integer setOfNotesId) {
        this.setOfNotesId = setOfNotesId;
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
