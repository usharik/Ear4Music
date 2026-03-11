package ru.usharik.ear4music.model.room;

import androidx.room.Embedded;
import androidx.room.Relation;

import ru.usharik.ear4music.model.SubTask;
import ru.usharik.ear4music.model.Task;

import java.util.List;

public class TaskWithSubTasks {
    @Embedded
    public Task task;

    @Relation(parentColumn = "id", entityColumn = "taskId")
    public List<SubTask> subTasks;
}