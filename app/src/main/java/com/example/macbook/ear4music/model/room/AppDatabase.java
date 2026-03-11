package com.example.macbook.ear4music.model.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.macbook.ear4music.model.Task;
import com.example.macbook.ear4music.model.SubTask;

@Database(entities = {Task.class, SubTask.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "ear4-music-db-room";

    public static AppDatabase create(Context context) {
        return create(context, DATABASE_NAME);
    }

    public static AppDatabase create(Context context, String databaseName) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, databaseName)
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        Migrations.populateInitialData(db);
                    }
                })
                .build();
    }

    public abstract TaskRoomDao taskRoomDao();
    public abstract SubTaskRoomDao subTaskRoomDao();
}
