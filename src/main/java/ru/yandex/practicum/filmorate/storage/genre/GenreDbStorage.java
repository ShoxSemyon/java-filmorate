package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Primary
public class GenreDbStorage implements GenreStorage {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenre(long id) {
        String sql = "SELECT * FROM \"Genre\" WHERE \"id\" = ?";

        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> extraxctRating(rs), id);
        } catch (DataAccessException e) {
            throw new NotFoundException("Genre не найден");
        }

        return genre;
    }


    @Override
    public List<Genre> getAllGenre() {
        String sql = "SELECT * FROM \"Genre\"";

        return jdbcTemplate.query(sql, (rs, rowNum) -> extraxctRating(rs));
    }

    private Genre extraxctRating(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getLong("id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}
