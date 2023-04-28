package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmComparatorByLikeCount implements Comparator<Film> {
    @Override
    public int compare(Film o1, Film o2) {
        return o2.getUserLikeIds().size() - o1.getUserLikeIds().size();
    }
}
