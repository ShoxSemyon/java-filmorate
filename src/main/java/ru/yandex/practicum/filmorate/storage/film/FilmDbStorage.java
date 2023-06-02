package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

@Repository
@Slf4j
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Film film) {
        String sql = "INSERT INTO \"Film\" (\"name\", \"description\", \"release_date\", \"duration\", \"rating\")\n"
                + "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stm = connection.prepareStatement(sql, new String[]{"id"});
            stm.setString(1, film.getName());
            stm.setString(2, film.getDescription());
            stm.setDate(3, Date.valueOf(film.getReleaseDate()));
            stm.setLong(4, film.getDuration().toMinutes());
            stm.setInt(5, ((int) film.getMpa().getId()));
            return stm;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        log.info("В H2 добавлен User c Id = " + film.getId());

    }

    @Override
    public void update(Film film) {
        String sql = "UPDATE \"Film\"\n" + "SET \"name\"        = ?,\n" + "    \"description\" = ?,\n" +
                "    \"duration\"    = ?,\n" + "    \"release_date\"=?,\n" + "    \"rating\"      = ?\n" +
                "WHERE \"id\" = ?";

        jdbcTemplate.update(sql, film.getName(),
                film.getDescription(),
                film.getDuration().toMinutes(),
                Date.valueOf(film.getReleaseDate()),
                film.getMpa().getId(),
                film.getId());

    }

    @Override
    public List<Film> loadFilms() {
        String sql = "SELECT F.*, R.\"name\" AS \"rating_name\", R.\"id\" AS \"rating_id\"\n" +
                "FROM \"Film\" F\n" +
                "         JOIN \"Rating\" R ON F.\"rating\" = R.\"id\"";

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> extractedFilm(resultSet));
    }


    @Override
    public Film loadFilm(long id) {
        String sql = "SELECT F.*, R.\"name\" AS \"rating_name\", R.\"id\" AS \"rating_id\"\n" +
                "FROM \"Film\" F\n" +
                "         JOIN \"Rating\" R ON F.\"rating\" = R.\"id\"\n" +
                "WHERE F.\"id\"=?";

        try {
            return jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> extractedFilm(resultSet), id);
        } catch (DataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public List<Film> loadPopularFilms(int count) {
        String sql = "SELECT F.*, R.\"name\" AS \"rating_name\", R.\"id\" AS \"rating_id\"\n" +
                "FROM \"Film\" F\n" +
                "         JOIN \"Rating\" R ON F.\"rating\" = R.\"id\"\n" +
                "         LEFT JOIN (SELECT \"film_id\", count(*) as countLike FROM \"User_like\" " +
                "GROUP BY \"film_id\") AS ULC ON \"film_id\" = F.\"id\"\n" +
                "ORDER BY countLike DESC\n" +
                "LIMIT ?\n";


        return jdbcTemplate.query(sql, (resultSet, rowNum) -> extractedFilm(resultSet), count);
    }

    private Film extractedFilm(ResultSet resultSet) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(Duration.ofMinutes(resultSet.getLong("duration")));
        film.setMpa(RatingDbStorage.extraxctRating(resultSet));
        return film;
    }
}

