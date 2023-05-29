package ru.yandex.practicum.filmorate.controller;

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
public class MpaController {
    private final RatingStorage ratingStorage;

    @Autowired
    public MpaController(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    @GetMapping("/{id}")
    public Rating getRating(@PathVariable long id) {

        return ratingStorage.getRating(id);
    }

    @GetMapping
    public List<Rating> getAllRating() {
        return ratingStorage.getAllRating();
    }
}
