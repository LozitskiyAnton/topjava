package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(100002, USER_ID);
        assertMatch(meal, meals.get(0));
    }

    @Test
    public void getForeignMeal() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(100002, ADMIN_ID));
    }

    @Test
    public void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void delete() throws Exception {
        service.delete(100002, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(100002, USER_ID));
    }

    @Test
    public void deleteForeignMeal() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(100003, ADMIN_ID));
    }

    @Test
    public void deleteNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> meals = service.getBetweenInclusive(
                LocalDate.of(2020, Month.JANUARY, 30),
                LocalDate.of(2020, Month.JANUARY, 30),
                USER_ID
        );
        assertMatch(meals, MealTestData.meals.subList(0, 3).stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed()).collect(Collectors.toList()));
    }

    @Test
    public void getAll() {
        List<Meal> meals = service.getAll(USER_ID);
        assertMatch(meals, MealTestData.meals.subList(0, 3).stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed()).collect(Collectors.toList()));
    }

    @Test
    public void update() {
        Meal updated = meals.get(1);
        updated.setDescription("Updated meal");
        service.update(updated, USER_ID);
        assertMatch(service.get(100003, USER_ID), updated);
    }

    @Test
    public void updateForeignMeal() throws Exception {
        Meal updated = meals.get(1);
        updated.setDescription("Updated foreign meal");
        assertThrows(NotFoundException.class, () -> service.update(updated, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal newMeal = MealTestData.getNew();
        Meal created = service.create(newMeal, MealTestData.USER_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        MealTestData.assertMatch(created, newMeal);
        MealTestData.assertMatch(service.get(newId, MealTestData.USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() throws Exception {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500), MealTestData.USER_ID));
    }
}