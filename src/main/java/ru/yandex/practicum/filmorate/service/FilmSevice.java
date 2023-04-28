package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.utils.FilmComparatorByLikeCount;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmSevice {

    private final FilmStorage storage;

    private final UserService uService;

    private long counter = 0;

    @Autowired
    public FilmSevice(FilmStorage storage, UserService uService) {
        this.storage = storage;
        this.uService = uService;
    }

    public Film addFilmInStorage(Film film) {

        if (validate(film)) {
            film.setId(++counter);

            storage.add(film);

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

        log.debug("Фильм обновлён " + film);

        return film;
    }

    public List<Film> getFilmFromStorage() {
        List<Film> films = storage.getAll();
        log.info("Кол-во фильмов {}", films.size());
        return films;
    }

    public void addLike(long id, long userId) {
        Film film = getFilm(id);
        User user = uService.getUser(userId);

        film.setLike(userId);

    }

    public void deleteLike(long id, long userId) {
        Film film = getFilm(id);
        User user = uService.getUser(userId);

        film.deleteLike(userId);
    }

    public List<Film> getPopular(int count) {
        return storage.getAll()
                .stream()
                .sorted(new FilmComparatorByLikeCount())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilm(long id) {
        Optional<Film> us = storage.getAll()
                .stream()
                .filter(film -> film.getId() == id)
                .findFirst();

        if (us.isPresent()) {

            return us.get();
        } else {

            throw new NotFoundException(String.format("Фильм c Id = %s не найден", id));
        }

    }

    protected boolean validate(Film film) {
        if (film.getUserLikeIds() == null) film.setUserLikeIds(new HashSet<>());

        return film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))
                && film.getDuration().toMinutes() > 0;
    }

}
