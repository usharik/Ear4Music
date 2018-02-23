package com.example.macbook.ear4music.service;

import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.Task;

/**
 * Created by au185034 on 21/02/2018.
 */

public class AppState {
    private Task task;
    private SubTask subTask;
    private int currentTab;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public SubTask getSubTask() {
        return subTask;
    }

    public void setSubTask(SubTask subTask) {
        this.subTask = subTask;
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(int currentTab) {
        this.currentTab = currentTab;
    }
}
