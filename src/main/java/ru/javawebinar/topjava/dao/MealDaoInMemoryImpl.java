package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

import static ru.javawebinar.topjava.storage.StorageInMemory.meals;

public class MealDaoInMemoryImpl implements MealDao {

    @Override
    public Meal find(Integer id) {
        for (Meal meal : meals()) {
            if (meal.getId() == id) return meal;
        }
        return null;
    }

    @Override
    public void save(Meal meal) {
        meals().add(meal);
    }

    @Override
    public void update(Meal meal) {

    }

    @Override
    public void delete(Integer id) {
        meals().remove(this.find(id));
    }

    @Override
    public List<Meal> findAll() {
        return meals();
    }
}
