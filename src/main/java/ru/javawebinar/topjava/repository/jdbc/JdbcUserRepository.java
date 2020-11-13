package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbcUserRepository implements UserRepository {
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private Map<Integer, User> userMap;

    private final ResultSetExtractor<Map<Integer, User>> resultSetExtractor = resultSet ->
    {
        userMap = new LinkedHashMap<>();
        while (resultSet.next()) {
            Integer id = resultSet.getInt("id");
            if (!userMap.containsKey(id)) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Date date = resultSet.getDate("registered");
                Boolean enabled = resultSet.getBoolean("enabled");
                Integer cpd = resultSet.getInt(7);
                User user = new User(id, name, email, password, cpd, enabled, date, new HashSet<>());
                userMap.put(id, user);
            }
            Role role = Enum.valueOf(Role.class, resultSet.getString("roles"));
            userMap.get(id).getRoles().add(role);
        }
        return userMap;
    };

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private int[] batchInsert(User user) {
        return jdbcTemplate.batchUpdate("INSERT INTO user_roles (user_id, role) VALUES (?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setString(2, String.valueOf(user.getRoles().iterator().next()));
            }

            @Override
            public int getBatchSize() {
                return user.getRoles().size();
            }
        });
    }

    @Override
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else {
            if (namedParameterJdbcTemplate.update("""
                       UPDATE users SET name=:name, email=:email, password=:password, 
                       registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                    """, parameterSource) == 0) {
                return null;
            }
            jdbcTemplate.update("DELETE FROM user_roles  WHERE user_id=?", user.getId());
        }
        batchInsert(user);
        return user;
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        userMap = jdbcTemplate.query("SELECT *, ur.role roles FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE id=?", resultSetExtractor, id);
        return userMap.getOrDefault(id, null);
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = new ArrayList<>(jdbcTemplate.query("SELECT *, ur.role roles FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE email=?", resultSetExtractor, email).values());
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(jdbcTemplate.query("SELECT u.*, ur.role roles FROM users u LEFT JOIN  user_roles ur ON u.id = ur.user_id ORDER BY name, email", resultSetExtractor).values());
    }
}
