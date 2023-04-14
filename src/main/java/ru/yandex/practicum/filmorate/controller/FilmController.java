package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IllegalFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    Map<Integer, Film> films;
    int id;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    FilmController() {
        films = new HashMap<>();
        id = 0;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {

        if (validate(film)) {
            modificate(film);

            films.put(film.getId(), film);

            log.debug("Фильм добавлен " + film);

            return film;

        } else {
            throw new IllegalFilmException("Неправильные параметры");
        }
    }


    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        modificate(film);
        if (!films.containsKey(film.getId())) throw new FilmNotFoundException();
        if (!validate(film)) throw new IllegalFilmException("Неправильные параметры или фильма не существует");

        films.put(film.getId(), film);

        log.debug("Фильм обновлён " + film);

        return film;


    }

    @GetMapping
    public List<Film> getFilm() {
        return new ArrayList<>(films.values());
    }

    private void modificate(Film film) {
        if (film.getId() == 0) film.setId(++id);
    }

    protected boolean validate(Film film) {
        return film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))
                && film.getDuration().isPositive();
    }


}
