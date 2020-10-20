package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

import static org.assertj.core.api.Assertions.assertThat;

public class MealTestData {
    public static final int NOT_FOUND_MEAL = 10;
    public static final Meal userMeal1 = new Meal(START_SEQ+2, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак Юзера", 500);
    public static final Meal userMeal2 = new Meal(START_SEQ+3, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед Юзера", 1000);
    public static final Meal userMeal3 = new Meal(START_SEQ+4, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин Юзера", 500);
    public static final Meal userMeal4 = new Meal(START_SEQ+5, LocalDateTime.of(2020, Month.JANUARY, 31, 23, 0), "Ночной перекус Юзера", 300);
    public static final Meal userMeal5 = new Meal(START_SEQ+6, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение Админа", 100);
    public static final Meal userMeal6 = new Meal(START_SEQ+7, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак Админа", 1000);
    public static final Meal userMeal7 = new Meal(START_SEQ+8, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед Админа", 500);
    public static final Meal userMeal8 = new Meal(START_SEQ+9, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин Админа", 410);
    public static final Meal userMeal9 = new Meal(START_SEQ+10, LocalDateTime.of(2020, Month.JANUARY, 30, 23, 0), "Поздний Ужин Админа", 300);

    public static Meal getNewMeal() {
        return new Meal(null, userMeal1.getDateTime(), "Завтрак админа", 500);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
