package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Primary
public class RatingDbStorage implements RatingStorage {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static Rating extraxctRating(ResultSet rs) throws SQLException {
        Rating rating1 = new Rating();
        rating1.setId(rs.getLong("id"));
        rating1.setName(rs.getString("name"));
        return rating1;
    }

    @Override
    public Rating getRating(long id) {
        String sql = "SELECT * FROM \"Rating\" WHERE \"id\" = ?";

        Rating rating;
        try {
            rating = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> extraxctRating(rs), id);
        } catch (DataAccessException e) {
            throw new NotFoundException("Rating не найден");
        }

        return rating;
    }

    @Override
    public List<Rating> getAllRating() {
        String sql = "SELECT * FROM \"Rating\"";

        return jdbcTemplate.query(sql, (rs, rowNum) -> extraxctRating(rs));
    }
}
