package com.example.macbook.ear4music.model.room;

import com.example.macbook.ear4music.model.DaoSession;
import com.example.macbook.ear4music.model.SubTaskDao;
import com.example.macbook.ear4music.model.TaskDao;

import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.Task;

import java.util.List;

/**
 * Compatibility wrapper to present RoomDaoSession as the old DaoSession interface.
 */
public class RoomDaoSessionCompat implements DaoSession {
    private final RoomDaoSession roomSession;

    public RoomDaoSessionCompat(RoomDaoSession roomSession) {
        this.roomSession = roomSession;
    }

    @Override
    public TaskDao getTaskDao() {
        return new TaskDao() {
            @Override
            public List<Task> getAll() { return roomSession.getAllTasks(); }
            @Override
            public Task findById(long id) { return roomSession.findTaskById(id); }
            @Override
            public long insert(Task task) { return roomSession.insertTask(task); }
            @Override
            public void update(Task task) { roomSession.updateTask(task); }
        };
    }

    @Override
    public SubTaskDao getSubTaskDao() {
        return new SubTaskDao() {
            @Override
            public List<SubTask> findByTaskId(long taskId) { return roomSession.findSubTasksByTaskId(taskId); }
            @Override
            public SubTask findById(long id) { return roomSession.findSubTaskById(id); }
            @Override
            public long insert(SubTask subTask) { return roomSession.insertSubTask(subTask); }
            @Override
            public void update(SubTask subTask) { roomSession.updateSubTask(subTask); }
        };
    }

    @Override
    public <T> com.example.macbook.ear4music.model.QueryBuilder<T> queryBuilder(Class<T> entityClass) {
        // Minimal no-op QueryBuilder implementation for compatibility; only supports list() returning all entities.
        return new com.example.macbook.ear4music.model.QueryBuilder<T>() {
            @Override
            public com.example.macbook.ear4music.model.QueryBuilder<T> where(Object condition) { return this; }
            @Override
            public java.util.List<T> list() { return java.util.Collections.emptyList(); }
        };
    }

    @Override
    public void update(Object entity) {
        if (entity instanceof Task) roomSession.updateTask((Task) entity);
        else if (entity instanceof SubTask) roomSession.updateSubTask((SubTask) entity);
    }
}
