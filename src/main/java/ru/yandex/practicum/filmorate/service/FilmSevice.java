package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.utils.FilmComparatorByLikeCount;
import ru.yandex.practicum.filmorate.utils.GenresComparator;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmSevice {

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

        if (validate(film)) {

            storage.add(film);
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                genreStorage.batchUpdateGenres(new ArrayList<>(film.getGenres()), film.getId());
            }

            log.debug("Фильм добавлен " + film);

            return film;

        } else {
            throw new ValidationException("Невалидные параметры" + film.getId());
        }
    }

    public Film updateFilmInStorage(Film film) {

        getFilm(film.getId());

        if (!validate(film)) throw new ValidationException("Невалидные параметры" + film);

        storage.update(film);

        genreStorage.batchDeleteGenres(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreStorage.batchUpdateGenres(new ArrayList<>(film.getGenres()), film.getId());
        }

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

    protected boolean validate(Film film) {
        if (film.getUserLikeIds() == null) film.setUserLikeIds(new HashSet<>());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Genre> set = new TreeSet<>(new GenresComparator());
            set.addAll(film.getGenres());
            film.setGenres(set);
        }


        return film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))
                && film.getDuration().toMinutes() > 0;
    }

}
