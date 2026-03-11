package com.example.macbook.ear4music.model.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.macbook.ear4music.model.SubTask;

import java.util.List;

@Dao
public interface SubTaskRoomDao {
    @Query("SELECT * FROM sub_task WHERE id = :id LIMIT 1")
    SubTask findById(long id);

    @Transaction
    @Query("SELECT * FROM sub_task WHERE id = :id LIMIT 1")
    SubTaskWithTask findWithTaskById(long id);

    @Query("SELECT * FROM sub_task WHERE taskId = :taskId ORDER BY id")
    List<SubTask> findByTaskId(long taskId);

    @Query("SELECT * FROM sub_task WHERE isFavourite = 1 ORDER BY id")
    List<SubTask> findFavourites();

    @Insert
    long insert(SubTask subTask);

    @Update
    void update(SubTask subTask);
}

