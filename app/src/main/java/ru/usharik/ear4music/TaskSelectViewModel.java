package ru.usharik.ear4music;

import ru.usharik.ear4music.adapter.SubTaskAdapter;
import ru.usharik.ear4music.adapter.TaskAdapter;
import ru.usharik.ear4music.framework.ViewModelObservable;
import ru.usharik.ear4music.model.SubTask;
import ru.usharik.ear4music.model.Task;
import ru.usharik.ear4music.repository.SubTaskRepository;
import ru.usharik.ear4music.repository.TaskRepository;
import ru.usharik.ear4music.service.AppState;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by au185034 on 09/02/2018.
 */

public class TaskSelectViewModel extends ViewModelObservable {
    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final AppState appState;
    private int taskListPosition;
    private int favouriteTaskListPosition;

    @Inject
    public TaskSelectViewModel(final TaskRepository taskRepository,
                               final SubTaskRepository subTaskRepository,
                               final AppState appState) {
        this.taskRepository = taskRepository;
        this.subTaskRepository = subTaskRepository;
        this.appState = appState;
        this.taskListPosition = 0;
        this.favouriteTaskListPosition = 0;
    }

    TaskAdapter getTaskAdapter() {
        List<Task> taskList = new ArrayList<>();
        taskList.addAll(taskRepository.getAll());
        return new TaskAdapter(taskList);
    }

    SubTaskAdapter getFavouriteSubTaskAdapter() {
        List<SubTask> subTaskList = subTaskRepository.findFavourites();
        return new SubTaskAdapter(subTaskList, taskRepository.getAll(), subTaskRepository);
    }

    public void setTask(Task task) {
        appState.setTask(task);
    }

    public void setSubTask(SubTask subTask) {
        appState.setSubTask(subTask);
        if (subTask == null || subTask.getTaskId() <= 0L) {
            appState.setTask(null);
            return;
        }
        appState.setTask(taskRepository.findById(subTask.getTaskId()));
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
