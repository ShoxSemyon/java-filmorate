package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.UserComparatorById;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public void addFriend(long id, long friendId) {
        User user = storage.getUser(id);
        User friend = storage.getUser(friendId);


        user.setFriend(friendId);
        friend.setFriend(id);
    }

    public void deleteFriend(long id, long friendId) {
        User user = storage.getUser(id);
        User friend = storage.getUser(friendId);

        user.deleteFriend(friendId);
        friend.deleteFriend(id);

    }

    public List<User> getFriends(long id) {
        List<User> friendList = new ArrayList<>();
        User user = storage.getUser(id);
        if (!user.getMyFriend().isEmpty()) {
            friendList = getFriendList(user.getMyFriend());
        }

        return friendList;
    }

    public List<User> getCommonFriends(long id, long otherId) {
        final User user = storage.getUser(id);
        final User other = storage.getUser(otherId);
        final Set<Long> friends = user.getMyFriend();
        final Set<Long> otherFriends = other.getMyFriend();

        return friends.stream()
                .filter(otherFriends::contains)
                .map(storage::getUser)
                .collect(Collectors.toList());
    }

    public User getUser(long id) {
        return storage.getUser(id);

    }

    private List<User> getFriendList(Set<Long> frienList) {

        List<User> flist = new ArrayList<>(storage.getAll());

        flist = flist.stream()
                .filter(q -> frienList.contains(q.getId()))
                .collect(Collectors.toList());

        flist.sort(new UserComparatorById());

        return flist;
    }


    public User addUser(User user) {
        modificate(user);

        storage.update(user);

        log.debug("Пользователь добавлен " + user);

        return user;
    }

    public User updateUser(User user) {

        storage.getUser(user.getId());

        modificate(user);

        storage.update(user);

        log.debug("Пользователь обновлён " + user);

        return user;
    }

    public List<User> getUsers() {

        List<User> users = storage.getAll();
        log.info("Кол-во пользователей {}", users.size());

        return users;
    }

    private void modificate(User user) {
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        if (user.getMyFriend() == null) user.setMyFriend(new TreeSet<>());
    }
}
