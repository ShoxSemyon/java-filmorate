package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.Like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.utils.FilmComparatorByLikeCount;

import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmSevice {

    private static final int MAX_NAME_SIZE = 200;
    private static final ChronoLocalDate FILM_BIRTHDAY = ;
    private final FilmStorage storage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;


    @Autowired
    public FilmSevice(FilmStorage storage, GenreStorage genreStorage, LikeStorage likeStorage) {
        this.storage = storage;
        this.genreStorage = genreStorage;
        this.likeStorage = likeStorage;
    }

    public Film addFilmInStorage(Film film) {

        storage.add(film);
        genreStorage.saveGenres(film);
        log.debug("Фильм добавлен " + film);
        return film;

    }

    public Film updateFilmInStorage(Film film) {

        storage.getFilm(film.getId());
        validate(film);
        storage.update(film);
        genreStorage.saveGenres(film);
        log.debug("Фильм обновлён " + film);
        return film;
    }

    public List<Film> getFilmFromStorage() {
        List<Film> films = storage.getAll();

        genreStorage.loadGenres(films);
        likeStorage.loadLike(films);

        log.info("Кол-во фильмов {}", films.size());
        return films;
    }

    public void addLike(long id, long userId) {

        likeStorage.addLikeSiquence(id, userId);

    }

    public void deleteLike(long id, long userId) {

        likeStorage.deleteFilmLike(id, userId);
    }

    public List<Film> getPopular(int count) {
        List<Film> films = storage.getAll();

        genreStorage.loadGenres(films);
        likeStorage.loadLike(films);

        return films.stream()
                .sorted(new FilmComparatorByLikeCount())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilm(long id) {
        Film film = storage.getFilm(id);

        genreStorage.loadGenres(List.of(film));
        likeStorage.loadLike(List.of(film));

        return film;
    }

    protected void validate(Film film) {

        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Film name invalid");
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_NAME_SIZE) {
            throw new ValidationException("Film description invalid");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(FILM_BIRTHDAY)) {
            throw new ValidationException("Film release date invalid");
        }
        if (film.getDuration().toMinutes() <= 0) {
            throw new ValidationException("Film duration invalid");
        }
        final Rating mpa = film.getMpa();
        if (mpa == null || mpa.getId() ==0) {
            throw new ValidationException("Film mpa invalid");
        }
    }

}
