package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.FilmComparatorByLikeCount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    private long counter = 0;

    @Override
    public void save(Film film) {
        film.setId(++counter);

        films.put(film.getId(), film);
    }

    @Override
    public void update(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public List<Film> loadFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film loadFilm(long id) {
        if (films.containsKey(id)) {

            return films.get(id);
        } else {

            throw new NotFoundException(String.format("Фильм c Id = %s не найден", id));
        }

    }

    @Override
    public List<Film> loadPopularFilms(int count) {
        return loadFilms().stream()
                .sorted(new FilmComparatorByLikeCount())
                .limit(count)
                .collect(Collectors.toList());
    }
}
