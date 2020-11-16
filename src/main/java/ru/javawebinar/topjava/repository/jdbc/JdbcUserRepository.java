package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.*;
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
    private static final RowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final ResultSetExtractor<List<User>> resultSetExtractor = resultSet ->
    {
        Map<Integer, User> userMap = new LinkedHashMap<>();
        int i = 0;
        while (resultSet.next()) {
            Integer id = resultSet.getInt("id");
            User user = userMap.get(id);

            if (user == null) {
                user = ROW_MAPPER.mapRow(resultSet, i++);
                userMap.put(id, user);
            } else {
                i++;
            }
            String roles = resultSet.getString("roles");
            if (!(roles == null) && user != null) {
                EnumSet<Role> roleEnumSet = EnumSet.of(Enum.valueOf(Role.class, roles));
                if (!user.getRoles().isEmpty()) roleEnumSet.addAll(user.getRoles());
                user.setRoles(roleEnumSet);
            }
        }
        return new ArrayList<>(userMap.values());
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
        ValidationUtil.validate(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

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
        batchRolesInsert(user);
        return user;
    }

    private void batchRolesInsert(User user) {
        Iterator<Role> role = user.getRoles().iterator();
        jdbcTemplate.batchUpdate("INSERT INTO user_roles (user_id, role) VALUES (?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setString(2, role.next().name());
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
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT u.*, ur.role roles FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE id=?", resultSetExtractor, id));
    }

    @Override
    public User getByEmail(String email) {
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT u.*, ur.role roles FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE email=?", resultSetExtractor, email));
    }

    @Override
    public List<User> getAll() {
        return (jdbcTemplate.query("SELECT u.*, ur.role roles FROM users u LEFT JOIN  user_roles ur ON u.id = ur.user_id ORDER BY name, email", resultSetExtractor));
    }
}
