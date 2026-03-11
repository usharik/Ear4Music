package ru.usharik.ear4music.model.room;

import androidx.room.Embedded;
import androidx.room.Relation;

import ru.usharik.ear4music.model.SubTask;
import ru.usharik.ear4music.model.Task;

public class SubTaskWithTask {
    @Embedded
    public SubTask subTask;

    @Relation(parentColumn = "taskId", entityColumn = "id")
    public Task task;
}