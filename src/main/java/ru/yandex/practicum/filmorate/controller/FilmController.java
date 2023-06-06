package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmSevice;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmSevice service;

    @Autowired
    public FilmController(FilmSevice service) {
        this.service = service;
    }


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("Начало добавление фильма с параметрами {}", film);

        return service.addFilm(film);
    }


    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Начало обновление фильма с параметрами {}", film);

        return service.updateFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("Начало запроса всех фильмов");

        return service.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        log.debug("Начало запроса фильма по id= {}", id);

        return service.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Начало добавления Like для фильма id = {} пользователем id = {}", id, userId);

        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Начало удаления Like для фильма id = {} пользователем id = {}", id, userId);

        service.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") String count) {
        log.debug("Начало запроса {} самых популярых фильмов", count);

        return service.getPopular(Integer.parseInt(count));
    }
}
