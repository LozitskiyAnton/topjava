package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        for (Meal meal : MealsUtil.meals) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
        }
    }

    @Override
    public Meal save(int authUserId, Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(authUserId);
            log.info("create {}", meal);
            repository.put(meal.getId(), meal);
            return meal;
        }
        if (checkId(meal.getId(), authUserId)) {
            log.info("update {}", meal);
            repository.replace(meal.getId(), meal);
            return meal;
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(int authUserId, int id) {
        log.info("delete {}", id);
        if (checkId(id, authUserId)) {
            return repository.remove(id) != null;
        } else {
            return false;
        }
    }

    @Override
    public Meal get(int authUserId, int id) {
        log.info("get {}", id);
        if (checkId(id, authUserId)) {
            return repository.get(id);
        }
        return null;
    }

    @Override
    public List<Meal> getAll(int authUserId, LocalDate startDate, LocalDate endDate) {
        log.info("getAll with date");
        return filterByPredicate(authUserId, meal -> DateTimeUtil.isBetweenDate(meal.getDate(), startDate, endDate));
    }

    @Override
    public List<Meal> getAll(int authUserId) {
        log.info("getAll");
        return filterByPredicate(authUserId, meal -> true);
    }

    public List<Meal> filterByPredicate(int authUserId, Predicate<Meal> filter) {
        return repository.values().stream()
                .filter(meal -> (meal.getUserId() == authUserId))
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDate).reversed().thenComparing(Meal::getTime).reversed())
                .collect(Collectors.toList());
    }

    private boolean checkId(int id, int authUserId) {
        return repository.containsKey(id) && authUserId == repository.get(id).getUserId();
    }
}

