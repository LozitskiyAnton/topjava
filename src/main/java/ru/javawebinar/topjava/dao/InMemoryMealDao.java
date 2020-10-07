package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealDao implements CrudDao<Meal> {
    private final AtomicInteger counter = new AtomicInteger();
    private final Map<Integer, Meal> storage = new ConcurrentHashMap<>();

    {
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    private int nextId() {
        return counter.incrementAndGet();
    }

    @Override
    public Meal find(Integer id) {
        return storage.get(id);
    }

    @Override
    public Meal create(Meal meal) {
        Meal newMeal = new Meal(nextId(), meal.getDateTime(), meal.getDescription(), meal.getCalories());
        storage.put(newMeal.getId(), newMeal);
        return newMeal;
    }

    @Override
    public Meal update(Meal meal) {
        return storage.replace(meal.getId(), meal)==null ? null : meal ;
    }

    @Override
    public void delete(Integer id) {
        storage.remove(id);
    }

    @Override
    public List<Meal> findAll() {
        return new ArrayList<>(storage.values());
    }
}
