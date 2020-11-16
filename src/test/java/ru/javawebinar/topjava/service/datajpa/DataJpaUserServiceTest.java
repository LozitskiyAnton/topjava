package ru.javawebinar.topjava.service.datajpa;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(DATAJPA)
public class DataJpaUserServiceTest extends AbstractUserServiceTest {
    @Test
    public void getWithMeals() {
        User user = service.getWithMeals(USER_ID);
        USER_MATCHER.assertMatch(user, UserTestData.user);
        MealTestData.MEAL_MATCHER.assertMatch(user.getMeals(), MealTestData.meals);
    }

    @Test
    public void getWithMealsIfNoMeals() {
        User created = service.create(getNew());
        int newId = created.id();
        User newUser = service.getWithMeals(newId);
        User user = UserTestData.getNew();
        user.setId(newId);
        USER_MATCHER.assertMatch(newUser, user);
        MealTestData.MEAL_MATCHER.assertMatch(newUser.getMeals(), user.getMeals());
    }

    @Test
    public void getWithMealsWithRoles() {
        User user = service.getWithMeals(ADMIN_ID);
        USER_MATCHER.assertMatch(user, admin);
        MealTestData.MEAL_MATCHER.assertMatch(user.getMeals(), MealTestData.adminMeals);
    }

    @Test
    public void getWithMealsNotFound() {
        Assert.assertThrows(NotFoundException.class,
                () -> service.getWithMeals(1));
    }
}