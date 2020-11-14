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
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.ValidationUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final ResultSetExtractor<Map<Integer, User>> resultSetExtractor = resultSet ->
    {
        Map<Integer, User> userMap = new LinkedHashMap<>();
        while (resultSet.next()) {
            Integer id = resultSet.getInt("id");
            User user = userMap.get(id);
            if (user == null) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Date date = resultSet.getDate("registered");
                Boolean enabled = resultSet.getBoolean("enabled");
                Integer caloriesPerDay = resultSet.getInt("calories_per_day");
                user = new User(id, name, email, password, caloriesPerDay, enabled, date, new HashSet<>());
                userMap.put(id, user);
            }
            if (!(resultSet.getString("roles") == null)) {
                Role role = Enum.valueOf(Role.class, resultSet.getString("roles"));
                user.getRoles().add(role);
            }
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

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        ValidationUtil.validate(user);

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

    private void batchInsert(User user) {
        Iterator<Role> role = user.getRoles().iterator();
        jdbcTemplate.batchUpdate("INSERT INTO user_roles (user_id, role) VALUES (?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setString(2, String.valueOf(role.next()));
            }

            @Override
            public int getBatchSize() {
                return user.getRoles().size();
            }
        });
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT u.*, ur.role roles FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE id=?", resultSetExtractor, id).values());
    }

    @Override
    public User getByEmail(String email) {
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT u.*, ur.role roles FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE email=?", resultSetExtractor, email).values());
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(jdbcTemplate.query("SELECT u.*, ur.role roles FROM users u LEFT JOIN  user_roles ur ON u.id = ur.user_id ORDER BY name, email", resultSetExtractor).values());
    }
}
