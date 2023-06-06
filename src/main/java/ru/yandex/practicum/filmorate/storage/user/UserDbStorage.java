package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(User user) {
        String sql = "INSERT INTO \"User\"\n" + "(\"email\",\n" + " \"login\",\n" + " \"name\",\n" + " \"local_date\")\n" + "VALUES (?,\n" + "        ?,\n" + "        ?,\n" + "        ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stm = connection.prepareStatement(sql, new String[]{"id"});
            stm.setString(1, user.getEmail());
            stm.setString(2, user.getLogin());
            stm.setString(3, user.getName());
            stm.setDate(4, Date.valueOf(user.getBirthday()));
            return stm;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());

        log.info("В H2 добавлен User c Id = " + user.getId());
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE \"User\"\n" + "SET \"email\"      = ?,\n" + "    \"login\"      =?,\n" + "    \"name\"=?,\n" + "    \"local_date\" =?\n" + "WHERE \"id\" = ?";

        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), user.getId());

        log.info("В H2 обнавлён User c Id = " + user.getId());
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT *\n" + "FROM \"User\"";

        List<User> users = jdbcTemplate.query(sql, (resultSet, rowNum) -> extractedUser(resultSet));

        users.forEach(user -> user.setMyFriend(getFriendSiquence(user.getId())));

        return users;

    }

    @Override
    public User getUser(long id) {
        String sql = "SELECT *\n" + "FROM \"User\"\n" + "WHERE \"id\"=?\n";

        User userResp = null;
        try {
            userResp = jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> extractedUser(resultSet), id);
        } catch (DataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }

        if (userResp != null) userResp.setMyFriend(getFriendSiquence(userResp.getId()));

        return userResp;
    }

    @Override
    public void addFriendSiquence(long userId, long friendId) {

        String sql = "SELECT COUNT(*)\n" + "FROM \"Friendship\"\n" + "WHERE \"user_id\" = ?\n" + "  AND \"friend_id\" = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, friendId, userId);

        FriendshipStatus status = FriendshipStatus.UNCONFIRMED;

        if (count > 0) {
            log.info("Сущетсвует запрос на дружбу ");
            status = FriendshipStatus.CONFIRMED;

            sql = "UPDATE \"Friendship\"\n" +
                    "SET \"status\" = ?\n" +
                    "WHERE \"user_id\" =? AND \"friend_id\" = ?";

            jdbcTemplate.update(sql, status.toString(), friendId, userId);
            log.info("Запрос подтверждён");

        } else {

            sql = "INSERT INTO \"Friendship\"\n" + "VALUES (?, ?, ?)";

            jdbcTemplate.update(sql, userId, friendId, status.toString());
            log.info("Добавлен запрос на дружбу");
        }
    }

    @Override
    public Map<Long, FriendshipStatus> getFriendSiquence(long userId) {
        String sql = "SELECT \"friend_id\" as \"id\",\n" +
                "       \"status\"\n" +
                "FROM \"Friendship\"\n" +
                "WHERE \"user_id\" = ?\n" +
                "UNION\n" +
                "SELECT \"user_id\" as \"id\",\n" +
                "       \"status\"\n" +
                "FROM \"Friendship\"\n" +
                "WHERE \"friend_id\" = ?\n" +
                "  AND \"status\" = ?";

        return jdbcTemplate.query(sql, rs -> {
            Map<Long, FriendshipStatus> data = new HashMap<>();
            while (rs.next()) {
                data.putIfAbsent(rs.getLong("id"), FriendshipStatus.valueOf(rs.getString("status")));
            }
            return data;
        }, userId, userId, FriendshipStatus.CONFIRMED.toString());
    }

    @Override
    public void deleteFriendSiquence(long userId, long friendId) {
        String sql = "DELETE\n" + "FROM \"Friendship\"\n" + "WHERE (\"user_id\" = ?\n" + "    AND \"friend_id\" = ?)\n" + "   OR (\"user_id\" = ?\n" + "    AND \"friend_id\" = ?)\n";
        jdbcTemplate.update(sql, userId, friendId, friendId, userId);

    }

    private User extractedUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("name"));
        user.setLogin(resultSet.getString("login"));
        user.setEmail(resultSet.getString("email"));
        user.setBirthday(resultSet.getDate("local_date").toLocalDate());
        return user;
    }

}
