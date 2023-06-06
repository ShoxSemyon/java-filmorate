package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.utils.GenresComparator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Repository
@Primary
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static Genre extraxctGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getLong("id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

    @Override
    public Genre getGenre(long id) {
        String sql = "SELECT * FROM \"Genre\" WHERE \"id\" = ?";

        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> extraxctGenre(rs), id);
        } catch (DataAccessException e) {
            throw new NotFoundException("Genre не найден");
        }

        return genre;
    }

    @Override
    public void loadGenres(List<Film> films) {
        films.forEach(film -> film.setGenres(new TreeSet<>(new GenresComparator())));

        if (films.size() < 1) return;

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        final String sqlQuery = "select g.\"id\",\n" +
                "       g.\"name\",\n" +
                "       fg.\"film_id\",\n" +
                "       fg.\"genre_id\"\n" +
                "from \"Genre\" g, \"Film_genre\" fg where fg.\"genre_id\" = g.\"id\" AND fg.\"film_id\" " +
                "in (" + inSql + ")";


        jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            final Film film = filmById.get(rs.getLong("film_id"));
            film.setGenre(extraxctGenre(rs));
            return film;
        }, films.stream().map(Film::getId).toArray());
    }

    @Override
    public List<Genre> getAllGenre() {
        String sql = "SELECT * FROM \"Genre\"";

        return jdbcTemplate.query(sql, (rs, rowNum) -> extraxctGenre(rs));
    }

    @Override
    public void saveGenres(Film film) {
        if (film.getGenres() == null) return;

        String sqlDelete = "DELETE\n" + "FROM \"Film_genre\"\n" + "WHERE \"film_id\" = ?";
        jdbcTemplate.update(sqlDelete, film.getId());

        String sqlUpadte = "INSERT INTO \"Film_genre\"(\"film_id\", \"genre_id\")\n" + "VALUES (?,?)";
        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sqlUpadte, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
        log.info("Список жанров обнавлён фильма с id=" + film.getId());
    }

}
