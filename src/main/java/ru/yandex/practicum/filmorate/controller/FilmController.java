package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private long counter = 0;


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {

        if (validate(film)) {
            film.setId(++counter);

            films.put(film.getId(), film);

            log.debug("Фильм добавлен " + film);

            return film;

        } else {
            throw new ValidationException("Невалидные параметры" + film.getId());
        }
    }


    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {

        if (!films.containsKey(film.getId()))
            throw new NotFoundException("Фильма не существует с id = " + film.getId());
        if (!validate(film)) throw new ValidationException("Невалидные параметры" + film);

        films.put(film.getId(), film);

        log.debug("Фильм обновлён " + film);

        return film;


    }

    @GetMapping
    public List<Film> getFilm() {
        log.info("Кол-во фильмов {}", films.size());
        return new ArrayList<>(films.values());
    }


    protected boolean validate(Film film) {
        return film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))
                && film.getDuration().toMinutes() > 0;
    }


}
