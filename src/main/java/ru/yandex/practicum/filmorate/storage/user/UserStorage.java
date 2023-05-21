package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    void add(User user);

    void update(User user);

    List<User> getAll();

    User getUser(long id);

    void addFriendSiquence(long userId, long friendId);

    Map<Long, FriendshipStatus> getFriendSiquence(long userId);

    void deleteFriendSiquence(long userId, long friendId);
}
