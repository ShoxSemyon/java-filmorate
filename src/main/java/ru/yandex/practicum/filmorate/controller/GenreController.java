package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/genres")

public class GenreController {

    @GetMapping
    public List<Genre> getAllGenre() {
        return Arrays.asList(Genre.values());
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable long id) {
        return Arrays.stream(Genre.values())
                .filter(genre -> genre.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Жанр не найден"));
    }
}
