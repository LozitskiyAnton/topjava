package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class UserTestData {
    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int NOT_FOUND = 10;
    public static final User user = new User(USER_ID, "User", "user@yandex.ru", "password", Role.USER);
    public static final User admin = new User(ADMIN_ID, "Admin", "admin@gmail.com", "admin", Role.ADMIN, Role.USER);
    public static TestMatcher<User> USER_MATCHER = TestMatcher.usingIgnoringFieldsComparator("registered", "meals");

    public static User getNew() {
        return new User(null, "New", "new@gmail.com", "newPass", 1555, false, new Date(), Collections.singleton(Role.USER));
    }

    public static User getNewWithRoles() {
        return new User(null, "NewWithRoles", "newWithRoles@gmail.com", "newWithRolesPass", 1000, false, new Date(), Set.of(Role.ADMIN, Role.USER));
    }

    public static User getNewWithOutRoles() {
        return new User(null, "NewWithOutRoles", "newWithOutRoles@gmail.com", "newWithOutRolesPass", 1500, false, new Date(), null);
    }

    public static User getUpdated() {
        User updated = new User(user);
        updated.setEmail("update@gmail.com");
        updated.setName("UpdatedName");
        updated.setCaloriesPerDay(330);
        updated.setPassword("newPass");
        updated.setEnabled(false);
        updated.setRoles(Collections.singletonList(Role.ADMIN));
        return updated;
    }

    public static User getUpdatedWithDeletedRoles() {
        User updated = new User(user);
        updated.setEmail("updateWithDeletedRoles@gmail.com");
        updated.setName("UpdatedWithDeletedRolesName");
        updated.setCaloriesPerDay(330);
        updated.setPassword("newPass");
        updated.setEnabled(false);
        updated.setRoles(null);
        return updated;
    }

}
