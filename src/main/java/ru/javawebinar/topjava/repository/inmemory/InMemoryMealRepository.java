package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Meal save(int authUserId, Meal meal) {
        log.info("save {}", meal);
        if (authUserId == meal.getUserId()) {
            if (meal.isNew()) {
                meal.setId(counter.incrementAndGet());
                repository.put(meal.getId(), meal);
                return meal;
            } else {
                if (authUserId == repository.get(meal.getId()).getUserId()) {
                    repository.replace(meal.getId(), meal);
                    return meal;
                }
            }
        }
        return null;
    }

    @Override
    public boolean delete(int authUserId, int id) {
        log.info("delete {}", id);
        if (authUserId == repository.get(id).getUserId()) {
            return repository.remove(id) != null;
        } else {
            return false;
        }
    }

    @Override
    public Meal get(int authUserId, int id) {
        log.info("get {}", id);
        if (authUserId == repository.get(id).getUserId()) {
            return repository.get(id);
        }
        return null;
    }

    @Override
    public List<Meal> getAll(int authUserId, LocalDate startDate, LocalDate endDate) {
        log.info("getAll with date");
        return repository.values().stream()
                .filter(meal -> (meal.getUserId() == authUserId))
                .filter(meal -> DateTimeUtil.isBetweenDate(meal.getDate(), startDate, endDate))
                .sorted(Comparator.comparing(Meal::getDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAll(int authUserId) {
        log.info("getAll");
        return repository.values().stream()
                .filter(meal -> (meal.getUserId() == authUserId))
                .sorted(Comparator.comparing(Meal::getDate).reversed())
                .collect(Collectors.toList());
    }
}

