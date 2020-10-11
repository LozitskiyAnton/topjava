package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        if (meals.isEmpty()) {
            throw new NotFoundException("Meal not found");
        }
        return getTos(meals, SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getAll(int authUserId, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        List<Meal> meals = repository.getAll(authUserId, startDate, endDate);
        if (meals.isEmpty()) {
            throw new NotFoundException("Meal not found");
        }
        return getFilteredTos(meals, SecurityUtil.authUserCaloriesPerDay(), startTime, endTime);
    }

    public MealTo get(int authUserId, int id) {
        Meal meal = checkNotFoundWithId(repository.get(authUserId, id), id);
        return new MealTo(id, meal.getDateTime(), meal.getDescription(), meal.getCalories(), false);
    }

    public boolean delete(int authUserId, int id) {
        checkNotFoundWithId(repository.delete(authUserId, id), id);
        return true;
    }

    public void update(int authUserId, Meal meal) {
        checkNotFound(repository.save(authUserId, meal), "userId: " + authUserId);
    }

    public MealTo create(int authUserId, Meal meal) {
        Meal meal1 = checkNotFound(repository.save(authUserId, meal), "userId: " + authUserId);
        return new MealTo(meal1.getId(), meal1.getDateTime(), meal1.getDescription(), meal1.getCalories(), false);
    }

    public static List<MealTo> getTos(Collection<Meal> meals, int caloriesPerDay) {
        return filterByPredicate(meals, caloriesPerDay, meal -> true);
    }

    public static List<MealTo> getFilteredTos(Collection<Meal> meals, int caloriesPerDay, LocalTime startTime, LocalTime endTime) {
        return filterByPredicate(meals, caloriesPerDay, meal -> DateTimeUtil.isBetweenHalfOpenTime(meal.getTime(), startTime, endTime));
    }

    public static List<MealTo> filterByPredicate(Collection<Meal> meals, int caloriesPerDay, Predicate<Meal> filter) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories)));

        return meals.stream()
                .filter(filter)
                .map(meal -> new MealTo(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }


}