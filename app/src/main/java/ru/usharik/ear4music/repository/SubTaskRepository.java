package ru.usharik.ear4music.repository;

import ru.usharik.ear4music.model.SubTask;
import ru.usharik.ear4music.model.room.AppDatabase;
import ru.usharik.ear4music.model.room.SubTaskRoomDao;
import ru.usharik.ear4music.model.room.SubTaskWithTask;

import java.util.List;

public class SubTaskRepository {
    private final SubTaskRoomDao subTaskRoomDao;

    public SubTaskRepository(AppDatabase appDatabase) {
        this(appDatabase.subTaskRoomDao());
    }

    public SubTaskRepository(SubTaskRoomDao subTaskRoomDao) {
        this.subTaskRoomDao = subTaskRoomDao;
    }

    public List<SubTask> findByTaskId(long taskId) {
        return subTaskRoomDao.findByTaskId(taskId);
    }

    public SubTask findById(long id) {
        return subTaskRoomDao.findById(id);
    }

    public SubTaskWithTask findWithTaskById(long id) {
        return subTaskRoomDao.findWithTaskById(id);
    }

    public List<SubTask> findFavourites() {
        return subTaskRoomDao.findFavourites();
    }

    public void update(SubTask subTask) {
        subTaskRoomDao.update(subTask);
    }
}