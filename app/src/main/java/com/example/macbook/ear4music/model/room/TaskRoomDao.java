package com.example.macbook.ear4music.model.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.macbook.ear4music.model.Task;

import java.util.List;

@Dao
public interface TaskRoomDao {
    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Query("SELECT * FROM task WHERE id = :id LIMIT 1")
    Task findById(long id);

    @Insert
    long insert(Task task);

    @Update
    void update(Task task);
}

