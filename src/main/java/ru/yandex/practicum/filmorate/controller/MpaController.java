package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    @GetMapping("/{id}")
    public Rating getRating(@PathVariable long id) {

        return Arrays.stream(Rating.values())
                .filter(rating -> rating.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Рэйтинг не найден"));
    }

    @GetMapping
    public List<Rating> getAllRating() {
        return Arrays.asList(Rating.values());
    }
}
