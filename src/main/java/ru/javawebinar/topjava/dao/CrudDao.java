package ru.javawebinar.topjava.dao;

import java.util.List;

public interface CrudDao<T> {
    T find(Integer id);

    T create(T model);

    T update(T model);

    void delete(Integer id);

    List<T> findAll();
}
