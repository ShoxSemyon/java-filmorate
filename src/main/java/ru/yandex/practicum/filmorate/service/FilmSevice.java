package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.utils.FilmComparatorByLikeCount;
import ru.yandex.practicum.filmorate.utils.GenresComparator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmSevice {

    private final FilmStorage storage;

    private final UserService uService;


    @Autowired
    public FilmSevice(FilmStorage storage, UserService uService) {
        this.storage = storage;
        this.uService = uService;
    }

    public Film addFilmInStorage(Film film) {

        if (validate(film)) {

            storage.add(film);

            log.debug("Фильм добавлен " + film);

            return film;

        } else {
            throw new ValidationException("Невалидные параметры" + film.getId());
        }
    }

    public Film updateFilmInStorage(Film film) {

        storage.getFilm(film.getId());

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
        Film film = storage.getFilm(id);
        uService.getUser(userId);

        storage.addLikeSiquence(id, userId);

    }

    public void deleteLike(long id, long userId) {
        Film film = storage.getFilm(id);
        User user = uService.getUser(userId);

        storage.deleteFilmLike(id, userId);
    }

    public List<Film> getPopular(int count) {
        return storage.getAll()
                .stream()
                .sorted(new FilmComparatorByLikeCount())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilm(long id) {
        return storage.getFilm(id);
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
