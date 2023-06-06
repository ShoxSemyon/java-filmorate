package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {
    private final RatingStorage ratingStorage;

    @Autowired
    public MpaController(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    @GetMapping("/{id}")
    public Rating getRating(@PathVariable long id) {
        log.info("Запрос рейтинга с id ={}", id);
        return ratingStorage.getRating(id);
    }

    @GetMapping
    public List<Rating> getAllRating() {
        log.info("Запрос всех рейтингов");
        return ratingStorage.getAllRating();
    }
}
