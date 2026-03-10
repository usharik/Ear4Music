package com.example.macbook.ear4music.model.room;

import android.content.Context;

import androidx.room.Room;

import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.Task;

import java.util.List;

public class RoomDaoSession {
    private final AppDatabase db;

    public RoomDaoSession(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "ear4-music-db-room")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public TaskRoomDao taskRoomDao() {
        return db.taskRoomDao();
    }

    public SubTaskRoomDao subTaskRoomDao() {
        return db.subTaskRoomDao();
    }

    // convenience methods
    public List<Task> getAllTasks() {
        return taskRoomDao().getAll();
    }

    public Task findTaskById(long id) {
        return taskRoomDao().findById(id);
    }

    public List<SubTask> findSubTasksByTaskId(long taskId) {
        return subTaskRoomDao().findByTaskId(taskId);
    }

    public SubTask findSubTaskById(long id) {
        SubTask subTask = subTaskRoomDao().findById(id);
        if (subTask != null) {
            Task task = findTaskById(subTask.getTaskId());
            subTask.setTask(task);
        }
        return subTask;
    }

    public List<SubTask> findFavouriteSubTasks() {
        return subTaskRoomDao().findFavourites();
    }

    public long insertTask(Task task) {
        return taskRoomDao().insert(task);
    }

    public long insertSubTask(SubTask subTask) {
        return subTaskRoomDao().insert(subTask);
    }

    public void updateSubTask(SubTask subTask) {
        subTaskRoomDao().update(subTask);
    }

    public void updateTask(Task task) {
        taskRoomDao().update(task);
    }
}
