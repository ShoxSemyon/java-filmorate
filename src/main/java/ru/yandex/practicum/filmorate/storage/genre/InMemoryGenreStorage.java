package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Arrays;
import java.util.List;

@Repository
public class InMemoryGenreStorage implements GenreStorage {
    List<Genre> genres = Arrays.asList(
            new Genre(1, "Комедия"),
            new Genre(2, "Драма"),
            new Genre(3, "Мультфильм"),
            new Genre(4, "Триллер"),
            new Genre(5, "Документальный"),
            new Genre(6, "Боевик")
    );

    @Override
    public Genre getGenre(long id) {
        Genre genre;
        try {
            genre = genres.get((int) id);
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
        return genre;
    }

    @Override
    public List<Genre> getAllGenre() {
        return genres;
    }
}
