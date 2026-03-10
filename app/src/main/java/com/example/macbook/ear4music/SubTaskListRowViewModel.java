package com.example.macbook.ear4music;

import com.example.macbook.ear4music.framework.ViewModelObservable;
import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.Task;
import com.example.macbook.ear4music.service.DbService;

/**
 * Created by macbook on 18.02.2018.
 */

public class SubTaskListRowViewModel extends ViewModelObservable {
    private final SubTask subTask;
    private final String subTaskDescription;
    private final DbService dbService;
    private final com.example.macbook.ear4music.model.DaoSession daoSession;

    public SubTaskListRowViewModel(final SubTask subTask,
                                   final String subTaskDescription,
                                   final DbService dbService) {
        this.subTask = subTask;
        this.subTaskDescription = subTaskDescription;
        this.dbService = dbService;
        this.daoSession = null;
    }

    // Compatibility constructor for code still passing DaoSession
    public SubTaskListRowViewModel(final SubTask subTask,
                                   final String subTaskDescription,
                                   final com.example.macbook.ear4music.model.DaoSession daoSession) {
        this.subTask = subTask;
        this.subTaskDescription = subTaskDescription;
        this.dbService = null;
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
        if (dbService != null) dbService.updateSubTask(subTask);
        else if (daoSession != null) daoSession.update(subTask);
    }

    public int getSetOfNotesId() {
        Task task = subTask.getTask();
        return subTask.isWithNoteHighlighting() ? task.getSetOfNotesHighlightingId() : task.getSetOfNotesId();
    }
}
