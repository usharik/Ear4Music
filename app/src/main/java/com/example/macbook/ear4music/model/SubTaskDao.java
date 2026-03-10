package com.example.macbook.ear4music.model;

import java.util.List;

public interface SubTaskDao {
    List<SubTask> findByTaskId(long taskId);
    SubTask findById(long id);
    long insert(SubTask subTask);
    void update(SubTask subTask);
}
