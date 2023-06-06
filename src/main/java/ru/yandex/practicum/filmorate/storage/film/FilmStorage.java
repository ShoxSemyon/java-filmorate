package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    void save(Film film);

    void update(Film film);

    List<Film> loadFilms();

    Film loadFilm(long id);

    List<Film> loadPopularFilms(int count);
}
