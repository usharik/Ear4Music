package com.example.macbook.ear4music.service;

import android.app.Application;

import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.Task;
import com.example.macbook.ear4music.model.room.RoomDaoSession;

import java.util.List;

/**
 * Created by au185034 on 23/02/2018.
 */

public class DbService {
    private final RoomDaoSession roomSession;

    public DbService(Application application) {
        roomSession = new RoomDaoSession(application);
    }

    public RoomDaoSession getRoomSession() {
        return roomSession;
    }

    // Compatibility: present an implementation of the old DaoSession interface backed by Room
    public com.example.macbook.ear4music.model.DaoSession getDaoSession() {
        return new com.example.macbook.ear4music.model.room.RoomDaoSessionCompat(roomSession);
    }

    // Backwards-compatible helpers used by the app
    public List<Task> getAllTasks() {
        return roomSession.getAllTasks();
    }

    public Task findTaskById(long id) {
        return roomSession.findTaskById(id);
    }

    public List<SubTask> findSubTasksByTaskId(long taskId) {
        return roomSession.findSubTasksByTaskId(taskId);
    }

    public SubTask findSubTaskById(long id) {
        return roomSession.findSubTaskById(id);
    }

    public List<SubTask> findFavouriteSubTasks() {
        return roomSession.findFavouriteSubTasks();
    }

    public void updateSubTask(SubTask subTask) {
        roomSession.updateSubTask(subTask);
    }

    public void updateTaskDonePercent(Task task) {
        List<SubTask> all = findSubTasksByTaskId(task.getId());
        int allCnt = all.size();
        int doneCnt = 0;
        for (SubTask s : all) {
            if (s.getCorrectAnswerPercent() != 0) doneCnt++;
        }
        if (allCnt > 0) {
            task.setDonePercent(doneCnt * 100 / allCnt);
            roomSession.updateTask(task);
        }
    }
}
