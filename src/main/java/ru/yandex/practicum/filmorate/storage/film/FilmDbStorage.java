package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Repository
@Slf4j
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(Film film) {
        String sql = "INSERT INTO \"Film\" (\"name\", \"description\", \"release_date\", \"duration\", \"rating\")\n" +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stm = connection.prepareStatement(sql, new String[]{"id"});
            stm.setString(1, film.getName());
            stm.setString(2, film.getDescription());
            stm.setDate(3, Date.valueOf(film.getReleaseDate()));
            stm.setLong(4, film.getDuration().toMinutes());
            stm.setString(5, film.getMpa().toString());
            return stm;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        log.info("В H2 добавлен User c Id = " + film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            batchUpdateGenres(new ArrayList<>(film.getGenres()), film.getId());
        }
    }

    @Override
    public void update(Film film) {
        String sql = "UPDATE \"Film\"\n" +
                "SET \"name\"        = ?,\n" +
                "    \"description\" = ?,\n" +
                "    \"duration\"    = ?,\n" +
                "    \"release_date\"=?,\n" +
                "    \"rating\"      = ?\n" +
                "WHERE \"id\" = ?";

        jdbcTemplate.update(sql, film.getName(),
                film.getDescription(),
                film.getDuration().toMinutes(),
                Date.valueOf(film.getReleaseDate()),
                film.getMpa().toString(),
                film.getId());

        batchDeleteGenres(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            batchUpdateGenres(new ArrayList<>(film.getGenres()), film.getId());
        }
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT *\n" +
                "FROM \"Film\"";

        List<Film> films = jdbcTemplate.query(sql,
                (resultSet, rowNum) -> extractedFilm(resultSet));

        films.forEach(film -> {
            film.setUserLikeIds(getLikeSiquence(film.getId()));
            film.setGenres(getGenre(film.getId()));
        });
        return films;
    }

    @Override
    public Film getFilm(long id) {
        String sql = "SELECT *\n" +
                "FROM \"Film\"\n" +
                "WHERE \"id\" = ?";

        Film film = null;
        try {
            film = jdbcTemplate.queryForObject(sql,
                    (resultSet, rowNum) -> extractedFilm(resultSet),
                    id);
        } catch (DataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
        film.setUserLikeIds(getLikeSiquence(film.getId()));
        film.setGenres(getGenre(film.getId()));
        return film;
    }

    @Override
    public void addLikeSiquence(long id, long userId) {
        String sql = "INSERT INTO \"User_like\"(\"film_id\", \"user_id\")\n" +
                "values (?, ?)";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteLikeSiquence(long id, long userId) {
        String sql = "DELETE\n" +
                "FROM \"User_like\"\n" +
                "WHERE \"film_id\" = ?\n" +
                "  AND \"user_id\" = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    private void batchUpdateGenres(List<Genre> genres, long filmId) {
        String sql = "INSERT INTO \"Genre\"(\"film_id\", \"name\")\n" +
                "VALUES (?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setString(2, genres.get(i).toString());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
        log.info("Список жанров обнавлён фильма с id=" + filmId);
    }

    private void batchDeleteGenres(long filmId) {
        String sql = "DELETE\n" +
                "FROM \"Genre\"\n" +
                "WHERE \"film_id\" = ?";
        jdbcTemplate.update(sql, filmId);
        log.info("Жанры удалены для фильтма с id=" + filmId);
    }

    private Set<Genre> getGenre(long id) {
        String sql = "SELECT \"name\"\n" +
                "FROM \"Genre\"\n" +
                "WHERE \"film_id\"=?";

        List<Genre> filmGenre = jdbcTemplate.query(sql,
                (rs, rowNum) -> Genre.valueOf(rs.getString("name")),
                id);

        return new TreeSet<>(filmGenre);
    }

    private Set<Long> getLikeSiquence(long id) {
        String sql = "SELECT \"user_id\"\n" +
                "FROM \"User_like\"\n" +
                "WHERE \"film_id\" = ?";

        List<Long> filmsLike = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getLong("user_id"),
                id);

        return new TreeSet<>(filmsLike);
    }

    private Film extractedFilm(ResultSet resultSet) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(Duration.ofMinutes(resultSet.getLong("duration")));
        film.setMpa(Rating.valueOf(resultSet.getString("rating")));
        return film;
    }
}

