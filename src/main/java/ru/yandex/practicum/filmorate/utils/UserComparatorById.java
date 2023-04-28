package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Comparator;

public class UserComparatorById implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        return ((int) (o1.getId() - o2.getId()));
    }
}
