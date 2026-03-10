package com.example.macbook.ear4music.model;

public interface DaoSession {
    TaskDao getTaskDao();
    SubTaskDao getSubTaskDao();

    // compatibility helper methods
    <T> QueryBuilder<T> queryBuilder(Class<T> entityClass);

    void update(Object entity);
}
