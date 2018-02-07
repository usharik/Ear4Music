package com.example.macbook.ear4music;

import com.example.macbook.ear4music.framework.ViewModelObservable;
import com.example.macbook.ear4music.model.DaoSession;
import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.Task;

/**
 * Created by macbook on 18.02.2018.
 */

public class SubTaskListRowViewModel extends ViewModelObservable {
    private SubTask subTask;
    private String subTaskDescription;
    private final DaoSession daoSession;

    public SubTaskListRowViewModel(final SubTask subTask,
                                   final String subTaskDescription,
                                   final DaoSession daoSession) {
        this.subTask = subTask;
        this.subTaskDescription = subTaskDescription;
        this.daoSession = daoSession;
    }

    public String getNotesPerMinute() {
        return Integer.toString(subTask.getNotesPerMinute());
    }

    public int getCorrectAnswerPercent() {
        return subTask.getCorrectAnswerPercent();
    }

    public String getSubTaskDescription() {
        return subTaskDescription;
    }

    public boolean isFavourite() {
        return subTask.isFavourite();
    }

    public void setFavourite(boolean favourite) {
        subTask.setFavourite(favourite);
        daoSession.getSubTaskDao().update(subTask);
    }

    public int getSetOfNotesId() {
        Task task = subTask.getTask();
        return subTask.isWithNoteHighlighting() ? task.getSetOfNotesHighlightingId() : task.getSetOfNotesId();
    }
}
