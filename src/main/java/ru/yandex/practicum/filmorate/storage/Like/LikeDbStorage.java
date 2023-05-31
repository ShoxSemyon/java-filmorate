package ru.yandex.practicum.filmorate.storage.Like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Repository
@Primary
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void addLikeSiquence(long id, long userId) {
        String sql = "INSERT INTO \"User_like\"(\"film_id\", \"user_id\")\n" + "values (?, ?)";
        try {
            jdbcTemplate.update(sql, id, userId);
        } catch (DataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public void deleteFilmLike(long id, long userId) {
        String sql = "DELETE\n" + "FROM \"User_like\"\n" + "WHERE \"film_id\" = ?\n" + "  AND \"user_id\" = ?";

        int rowCount = jdbcTemplate.update(sql, id, userId);
        if (rowCount == 0) throw new NotFoundException("Фильм или пользователь не найден");

    }

    @Override
    public void loadLike(List<Film> films) {
        films.forEach(film -> film.setUserLikeIds(new TreeSet<>()));

        if (films.size() < 1) return;

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        final String sqlQuery = "SELECT * FROM \"User_like\" WHERE \"film_id\" in (" + inSql + ")";


        jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            final Film film = filmById.get(rs.getLong("film_id"));
            film.setLike(rs.getLong("user_id"));
            return film;
        }, films.stream().map(Film::getId).toArray());
    }
}
