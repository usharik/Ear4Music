package ru.usharik.ear4music.model.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import ru.usharik.ear4music.model.Task;

import java.util.List;

@Dao
public interface TaskRoomDao {
    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Query("SELECT * FROM task WHERE id = :id LIMIT 1")
    Task findById(long id);

    @Transaction
    @Query("SELECT * FROM task WHERE id = :id LIMIT 1")
    TaskWithSubTasks findWithSubTasksById(long id);

    @Insert
    long insert(Task task);

    @Update
    void update(Task task);
}

