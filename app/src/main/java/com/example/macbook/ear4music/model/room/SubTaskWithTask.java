package com.example.macbook.ear4music.model.room;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.Task;

public class SubTaskWithTask {
    @Embedded
    public SubTask subTask;

    @Relation(parentColumn = "taskId", entityColumn = "id")
    public Task task;
}