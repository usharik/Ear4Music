package com.example.macbook.ear4music;

import com.example.macbook.ear4music.adapter.SubTaskAdapter;
import com.example.macbook.ear4music.framework.ViewModelObservable;
import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.Task;
import com.example.macbook.ear4music.repository.SubTaskRepository;
import com.example.macbook.ear4music.repository.TaskRepository;
import com.example.macbook.ear4music.service.AppState;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by macbook on 18.02.2018.
 */

public class SubTaskSelectViewModel extends ViewModelObservable {
    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final AppState appState;
    private Task task;
    private int subTaskListPosition;

    @Inject
    public SubTaskSelectViewModel(final TaskRepository taskRepository,
                                  final SubTaskRepository subTaskRepository,
                                  final AppState appState) {
        this.taskRepository = taskRepository;
        this.subTaskRepository = subTaskRepository;
        this.appState = appState;
        this.subTaskListPosition = 0;
    }

    public boolean syncWithTaskId(long taskId) {
        Task appStateTask = appState.getTask();
        if (appStateTask != null && appStateTask.getId() != null
                && (taskId <= 0L || appStateTask.getId() == taskId)) {
            setTask(appStateTask);
            return true;
        }

        if (taskId > 0L) {
            Task restoredTask = taskRepository.findById(taskId);
            if (restoredTask != null) {
                setTask(restoredTask);
                return true;
            }
        }

        setTask(null);
        return false;
    }

    private void updateAppState() {
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
        if (task == null || task.getId() == null) {
            return new SubTaskAdapter(Collections.emptyList(), Collections.emptyList(), subTaskRepository);
        }
        List<SubTask> subTaskList = subTaskRepository.findByTaskId(task.getId());
        return new SubTaskAdapter(subTaskList, Collections.singletonList(task), subTaskRepository);
    }

    public int getSubTaskListPosition() {
        return subTaskListPosition;
    }

    public SubTaskSelectViewModel setSubTaskListPosition(int subTaskListPosition) {
        this.subTaskListPosition = subTaskListPosition;
        return this;
    }
}
