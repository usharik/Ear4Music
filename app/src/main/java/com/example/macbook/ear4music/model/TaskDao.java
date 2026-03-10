package com.example.macbook.ear4music.model;

import java.util.List;

public interface TaskDao {
    List<Task> getAll();
    Task findById(long id);
    long insert(Task task);
    void update(Task task);
}
