package com.example.macbook.ear4music.model.room;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.Task;

import java.util.List;

public class TaskWithSubTasks {
    @Embedded
    public Task task;

    @Relation(parentColumn = "id", entityColumn = "taskId")
    public List<SubTask> subTasks;
}