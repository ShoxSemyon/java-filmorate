package ru.yandex.practicum.filmorate.storage.Like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeStorage {

    void addLikeSiquence(long id, long userId);

    void deleteFilmLike(long id, long userId);

    void loadLike(List<Film> films);
}
