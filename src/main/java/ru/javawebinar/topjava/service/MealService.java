package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.MealsUtil.getFilteredTos;
import static ru.javawebinar.topjava.util.MealsUtil.getTos;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFound;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public List<MealTo> getAll(int authUserId) {
        List<Meal> meals = repository.getAll(authUserId);
        return getTos(meals, SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getFilteredAll(int authUserId, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        List<Meal> meals = repository.getAll(authUserId, startDate, endDate);
        return getFilteredTos(meals, SecurityUtil.authUserCaloriesPerDay(), startTime, endTime);
    }

    public Meal get(int authUserId, int id) {
        return checkNotFoundWithId(repository.get(authUserId, id), id);
    }

    public boolean delete(int authUserId, int id) {
        checkNotFoundWithId(repository.delete(authUserId, id), id);
        return true;
    }

    public void update(int authUserId, Meal meal) {
        checkNotFound(repository.save(authUserId, meal), "userId: " + authUserId);
    }

    public Meal create(int authUserId, Meal meal) {
        return checkNotFound(repository.save(authUserId, meal), "userId: " + authUserId);
    }
}