package com.example.macbook.ear4music;

import com.example.macbook.ear4music.adapter.SubTaskAdapter;
import com.example.macbook.ear4music.adapter.TaskAdapter;
import com.example.macbook.ear4music.framework.ViewModelObservable;
import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.SubTaskDao;
import com.example.macbook.ear4music.model.Task;
import com.example.macbook.ear4music.model.TaskDao;
import com.example.macbook.ear4music.service.AppState;
import com.example.macbook.ear4music.service.DbService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by au185034 on 09/02/2018.
 */

public class TaskSelectViewModel extends ViewModelObservable {
    private List<Task> taskList;
    private final DbService dbService;
    private final AppState appState;
    private int taskListPosition;
    private int favouriteTaskListPosition;

    @Inject
    public TaskSelectViewModel(final DbService dbService,
                               final AppState appState) {
        this.dbService = dbService;
        this.appState = appState;
        this.taskListPosition = 0;
        this.favouriteTaskListPosition = 0;
    }

    TaskAdapter getTaskAdapter() {
        TaskDao taskDao = dbService.getDaoSession().getTaskDao();
        taskList = new ArrayList<>();
        taskList.addAll(taskDao.queryBuilder().list());
        return new TaskAdapter(taskList);
    }

    SubTaskAdapter getFavouriteSubTaskAdapter() {
        List<SubTask> subTaskList = dbService.getDaoSession().queryBuilder(SubTask.class)
                .where(SubTaskDao.Properties.IsFavourite.eq(true))
                .list();
        return new SubTaskAdapter(subTaskList, dbService.getDaoSession());
    }

    public void setTask(Task task) {
        appState.setTask(task);
    }

    public void setSubTask(SubTask subTask) {
        appState.setSubTask(subTask);
    }

    public int getCurrentTab() {
        return appState.getCurrentTab();
    }

    public void setCurrentTab(int currentTab) {
        appState.setCurrentTab(currentTab);
    }

    public int getTaskListPosition() {
        return taskListPosition;
    }

    public TaskSelectViewModel setTaskListPosition(int taskListPosition) {
        this.taskListPosition = taskListPosition;
        return this;
    }

    public int getFavouriteTaskListPosition() {
        return favouriteTaskListPosition;
    }

    public TaskSelectViewModel setFavouriteTaskListPosition(int favouriteTaskListPosition) {
        this.favouriteTaskListPosition = favouriteTaskListPosition;
        return this;
    }
}
