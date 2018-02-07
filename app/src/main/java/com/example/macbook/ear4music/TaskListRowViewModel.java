package com.example.macbook.ear4music;

import com.example.macbook.ear4music.framework.ViewModelObservable;
import com.example.macbook.ear4music.model.Task;

/**
 * Created by au185034 on 09/02/2018.
 */

public class TaskListRowViewModel extends ViewModelObservable {

    private long id;
    private int nameId;
    private int setOfNotesId;
    private int donePercent;

    public TaskListRowViewModel(Task task) {
        this.id = task.getId();
        this.nameId = task.getNameId();
        this.setOfNotesId = task.getSetOfNotesId();
        this.donePercent = task.getDonePercent();
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

    public int getDonePercent() {
        return donePercent;
    }

    public void setDonePercent(int donePercent) {
        this.donePercent = donePercent;
    }
}
