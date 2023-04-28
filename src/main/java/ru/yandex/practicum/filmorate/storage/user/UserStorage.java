package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    void addUserInStorage(User user);

    void updateUserInStorage(User user);

    List<User> getUserFromStorage();
}
