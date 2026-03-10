package com.example.macbook.ear4music.model;

public interface QueryBuilder<T> {
    QueryBuilder<T> where(Object condition);
    java.util.List<T> list();
}

