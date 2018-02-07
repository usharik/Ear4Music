package com.example.macbook.ear4music;

import com.example.macbook.ear4music.adapter.SubTaskAdapter;
import com.example.macbook.ear4music.framework.ViewModelObservable;
import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.SubTaskDao;
import com.example.macbook.ear4music.model.Task;
import com.example.macbook.ear4music.service.AppState;
import com.example.macbook.ear4music.service.DbService;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by macbook on 18.02.2018.
 */

public class SubTaskSelectViewModel extends ViewModelObservable {
    private List<SubTask> subTaskList;
    private final DbService dbService;
    private final AppState appState;
    private Task task;

    @Inject
    public SubTaskSelectViewModel(final DbService dbService,
                                  final AppState appState) {
        this.dbService = dbService;
        this.appState = appState;
    }

    public void syncWithAppState() {
        setTask(appState.getTask());
    }

    public void updateAppState() {
        appState.setTask(task);
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
        updateAppState();
    }

    public void setSubTask(SubTask subTask) {
        appState.setSubTask(subTask);
    }

    SubTaskAdapter getTaskAdapter() {
        subTaskList = dbService.getDaoSession().queryBuilder(SubTask.class)
                .where(SubTaskDao.Properties.TaskId.eq(task.getId()))
                .list();
        return new SubTaskAdapter(subTaskList, dbService.getDaoSession());
    }
}
