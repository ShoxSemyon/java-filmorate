package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre getGenre(long id);

    void loadGenres(List<Film> films);

    List<Genre> getAllGenre();

    void saveGenres(Film film);

}
