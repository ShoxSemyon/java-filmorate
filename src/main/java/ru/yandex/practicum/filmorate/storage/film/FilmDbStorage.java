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
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import ru.yandex.practicum.filmorate.utils.GenresComparator;

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
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, RatingStorage ratingStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingStorage = ratingStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public void add(Film film) {
        String sql = "INSERT INTO \"Film\" (\"name\", \"description\", \"release_date\", \"duration\", \"rating\")\n" + "values (?, ?, ?, ?, ?)";

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

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            batchUpdateGenres(new ArrayList<>(film.getGenres()), film.getId());
        }
    }

    @Override
    public void update(Film film) {
        String sql = "UPDATE \"Film\"\n" + "SET \"name\"        = ?,\n" + "    \"description\" = ?,\n" + "    \"duration\"    = ?,\n" + "    \"release_date\"=?,\n" + "    \"rating\"      = ?\n" + "WHERE \"id\" = ?";

        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getDuration().toMinutes(), Date.valueOf(film.getReleaseDate()), film.getMpa().getId(), film.getId());

        batchDeleteGenres(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            batchUpdateGenres(new ArrayList<>(film.getGenres()), film.getId());
        }
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT *\n" + "FROM \"Film\"";

        List<Film> films = jdbcTemplate.query(sql, (resultSet, rowNum) -> extractedFilm(resultSet));

        films.forEach(film -> {
            film.setUserLikeIds(new TreeSet<>());
            film.setGenres(new TreeSet<>(new GenresComparator()));
        });

        setUserLike(films);
        setGenres(films);
        return films;
    }

    private void setGenres(List<Film> films) {
        String sql = "SELECT *\n" + "FROM \"Film_genre\"\n";

        List<Genre> genres = genreStorage.getAllGenre();

        jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            films.forEach(film -> {
                try {
                    if (film.getId() == resultSet.getLong("film_id")) {
                        genres.forEach(genre -> {
                            try {
                                if (genre.getId() == resultSet.getLong("genre_id")) film.setGenre(genre);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            return null;
        });
    }

    private void setUserLike(List<Film> films) {
        String sql = "SELECT *\n" + "FROM \"User_like\"\n";

        jdbcTemplate.query(sql, (resultSet, rowNum) -> {

            films.forEach(film -> {
                try {
                    if (film.getId() == resultSet.getLong("film_id")) {
                        film.setLike(resultSet.getLong("user_id"));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            return null;
        });
    }

    @Override
    public Film getFilm(long id) {
        String sql = "SELECT *\n" + "FROM \"Film\"\n" + "WHERE \"id\" = ?";

        Film film = null;
        try {
            film = jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> extractedFilm(resultSet), id);
        } catch (DataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
        film.setUserLikeIds(getLikeSiquence(film.getId()));
        film.setGenres(getGenre(film.getId()));
        return film;
    }

    @Override
    public void addLikeSiquence(long id, long userId) {
        String sql = "INSERT INTO \"User_like\"(\"film_id\", \"user_id\")\n" + "values (?, ?)";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteFilmLike(long id, long userId) {
        String sql = "DELETE\n" + "FROM \"User_like\"\n" + "WHERE \"film_id\" = ?\n" + "  AND \"user_id\" = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    private void batchUpdateGenres(List<Genre> genres, long filmId) {
        String sql = "INSERT INTO \"Film_genre\"(\"film_id\", \"genre_id\")\n" + "VALUES (?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
        log.info("Список жанров обнавлён фильма с id=" + filmId);
    }

    private void batchDeleteGenres(long filmId) {
        String sql = "DELETE\n" + "FROM \"Film_genre\"\n" + "WHERE \"film_id\" = ?";
        jdbcTemplate.update(sql, filmId);
        log.info("Жанры удалены для фильтма с id=" + filmId);
    }

    private Set<Genre> getGenre(long id) {
        String sql = "SELECT \"genre_id\"\n" + "FROM \"Film_genre\"\n" + "WHERE \"film_id\"=?";

        Set<Genre> filmGenre = new TreeSet<>(new GenresComparator());
        filmGenre.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> genreStorage.getGenre(rs.getLong("genre_id")), id));

        return filmGenre;
    }

    private Set<Long> getLikeSiquence(long id) {
        String sql = "SELECT \"user_id\"\n" + "FROM \"User_like\"\n" + "WHERE \"film_id\" = ?";

        List<Long> filmsLike = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), id);

        return new TreeSet<>(filmsLike);
    }

    private Film extractedFilm(ResultSet resultSet) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(Duration.ofMinutes(resultSet.getLong("duration")));
        film.setMpa(ratingStorage.getRating(resultSet.getLong("rating")));
        return film;
    }
}

