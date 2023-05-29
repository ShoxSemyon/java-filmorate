package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Arrays;
import java.util.List;

@Repository
public class InMemoryRatingStorage implements RatingStorage {

    List<Rating> ratings = Arrays.asList(
            new Rating(1, "G"),
            new Rating(2, "PG"),
            new Rating(3, "PG-13"),
            new Rating(4, "R"),
            new Rating(5, "NC-17")
    );

    @Override
    public Rating getRating(long id) {
        Rating rating;
        try {
            rating = ratings.get((int) id);
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
        return rating;
    }

    @Override
    public List<Rating> getAllRating() {
        return ratings;
    }
}
