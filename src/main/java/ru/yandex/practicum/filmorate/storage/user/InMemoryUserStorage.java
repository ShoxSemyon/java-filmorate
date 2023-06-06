package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private long counter = 0;

    @Override
    public void add(User user) {
        user.setId(++counter);

        users.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);

    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(long id) {

        if (users.containsKey(id)) {

            return users.get(id);
        } else {

            throw new NotFoundException(String.format("Ползователь c Id = %s не найден", id));
        }

    }

    @Override
    public void addFriendSiquence(long userId, long friendId) {
        if (getUser(userId).getMyFriend().containsKey(friendId)
                && getUser(friendId).getMyFriend().containsKey(userId)) {

            getUser(userId).setFriend(friendId, FriendshipStatus.CONFIRMED);
            getUser(friendId).setFriend(userId, FriendshipStatus.CONFIRMED);
        } else {
            getUser(userId).setFriend(friendId, FriendshipStatus.UNCONFIRMED);
            getUser(friendId).setFriend(userId, FriendshipStatus.UNCONFIRMED);
        }
    }

    @Override
    public Map<Long, FriendshipStatus> getFriendSiquence(long userId) {
        return null;
    }


    @Override
    public void deleteFriendSiquence(long userId, long friendId) {
        users.get(userId).deleteFriend(friendId);
        users.get(friendId).deleteFriend(userId);
    }

}
