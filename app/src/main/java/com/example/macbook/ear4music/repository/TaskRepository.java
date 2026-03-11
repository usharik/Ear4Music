package com.example.macbook.ear4music.repository;

import com.example.macbook.ear4music.model.Task;
import com.example.macbook.ear4music.model.room.AppDatabase;
import com.example.macbook.ear4music.model.room.TaskRoomDao;
import com.example.macbook.ear4music.model.room.TaskWithSubTasks;

import java.util.List;

public class TaskRepository {
    private final TaskRoomDao taskRoomDao;

    public TaskRepository(AppDatabase appDatabase) {
        this(appDatabase.taskRoomDao());
    }

    public TaskRepository(TaskRoomDao taskRoomDao) {
        this.taskRoomDao = taskRoomDao;
    }

    public List<Task> getAll() {
        return taskRoomDao.getAll();
    }

    public Task findById(long id) {
        return taskRoomDao.findById(id);
    }

    public TaskWithSubTasks findWithSubTasksById(long id) {
        return taskRoomDao.findWithSubTasksById(id);
    }

    public void updateDonePercent(Task task) {
        if (task == null || task.getId() == null) {
            return;
        }

        TaskWithSubTasks taskWithSubTasks = findWithSubTasksById(task.getId());
        if (taskWithSubTasks == null || taskWithSubTasks.subTasks == null || taskWithSubTasks.subTasks.isEmpty()) {
            return;
        }

        int allCnt = taskWithSubTasks.subTasks.size();
        int doneCnt = 0;
        for (com.example.macbook.ear4music.model.SubTask subTask : taskWithSubTasks.subTasks) {
            if (subTask.getCorrectAnswerPercent() != 0) {
                doneCnt++;
            }
        }
        task.setDonePercent(doneCnt * 100 / allCnt);
        update(task);
    }

    public void update(Task task) {
        taskRoomDao.update(task);
    }
}