package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    int authUserId = SecurityUtil.authUserId();

    @Autowired
    private MealService service;

    public List<MealTo> getAll() {
        log.info("getAll");
        return service.getAll(authUserId);
    }

    public List<MealTo> getAll(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        log.info("filtered getAll ");
        return service.getAll(authUserId, startDate, startTime, endDate, endTime);
    }

    public MealTo get(int id) {
        log.info("get {}", id);
        return service.get(authUserId, id);
    }

    public boolean delete(int id) {
        log.info("delete {}", id);
        return service.delete(authUserId, id);
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        service.update(authUserId, meal);
    }

    public MealTo create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(authUserId, meal);
    }


}