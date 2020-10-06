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
        create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500, 0));
        create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000, 0));
        create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500, 0));
        create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100, 0));
        create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000, 0));
        create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500, 0));
        create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410, 0));
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
        Meal newMeal = new Meal(meal.getDateTime(), meal.getDescription(), meal.getCalories(), nextId());
        return storage.put(newMeal.getId(), newMeal);
    }

    @Override
    public Meal update(Meal meal) {
        return storage.replace(meal.getId(), meal);
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
