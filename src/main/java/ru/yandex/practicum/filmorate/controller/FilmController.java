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
    private final FilmSevice sevice;

    @Autowired
    public FilmController(FilmSevice sevice) {
        this.sevice = sevice;
    }


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return sevice.addFilmInStorage(film);
    }


    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return sevice.updateFilmInStorage(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        return sevice.getFilmFromStorage();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        return sevice.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        sevice.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        sevice.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") String count) {

        return sevice.getPopular(Integer.parseInt(count));
    }
}
