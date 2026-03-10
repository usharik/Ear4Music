package com.example.macbook.ear4music.model.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.macbook.ear4music.model.Task;
import com.example.macbook.ear4music.model.SubTask;

@Database(entities = {Task.class, SubTask.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskRoomDao taskRoomDao();
    public abstract SubTaskRoomDao subTaskRoomDao();
}
