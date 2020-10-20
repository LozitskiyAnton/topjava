DELETE FROM user_roles;
DELETE FROM meals;
DELETE FROM users;

ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (date_time, description, calories, user_id)
VALUES ('2020-01-30 10:00:00', 'Завтрак Юзера', 500, 100000),
       ('2020-01-30 13:00:00', 'Обед Юзера', 1000, 100000),
       ('2020-01-30 20:00:00', 'Ужин Юзера', 500, 100000),
       ('2020-01-31 23:00:00', 'Ночной перекус Юзера', 300, 100000),
       ('2020-01-31 00:00:00', 'Еда на граничное значение Админа', 100, 100001),
       ('2020-01-31 10:00:00', 'Завтрак Админа', 1000, 100001),
       ('2020-01-31 13:00:00', 'Обед Админа', 500, 100001),
       ('2020-01-31 20:00:00', 'Ужин Админа', 410, 100001),
       ('2020-01-30 23:00:00', 'Поздний Ужин Админа', 300, 100001);
