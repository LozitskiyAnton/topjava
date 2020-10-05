package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealDaoInMemory implements CrudDao<Meal> {
    private final static AtomicInteger counter = new AtomicInteger();
    private static final Map<Integer, Meal> storage = new ConcurrentHashMap<>();
    private static final List<Meal> meals;

    static {
        meals = new ArrayList<>(Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500, nextId()),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000, nextId()),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500, nextId()),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100, nextId()),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000, nextId()),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500, nextId()),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410, nextId())
        ));
        for (Meal meal : meals) {
            storage.put(meal.getId(), meal);
        }
    }

    public static int nextId() {
        return counter.incrementAndGet();
    }

    @Override
    public Meal find(Integer id) {
        return storage.get(id);
    }

    @Override
    public Meal create(Meal meal) {
        return storage.put(meal.getId(), meal);
    }

    @Override
    public Meal update(Meal meal) {
        return storage.put(meal.getId(), meal);
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
