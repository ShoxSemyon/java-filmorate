package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.UserComparatorById;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {
    private final UserStorage storage;
    private long counter = 0;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public void addFriend(long id, long friendId) {
        User user = getUser(id);
        User friend = getUser(friendId);


        user.setFriend(friendId);
        friend.setFriend(id);

        System.out.println(user);
        System.out.println(friend);
    }

    public void deleteFriend(long id, long friendId) {
        User user = getUser(id);
        User friend = getUser(friendId);

        user.deleteFriend(friendId);
        friend.deleteFriend(id);

    }

    public List<User> getFriends(long id) {
        List<User> friendList = new ArrayList<>();
        User user = getUser(id);
        if (!user.getMyFriend().isEmpty()) {
            friendList = getFriendList(user.getMyFriend());
        }

        return friendList;
    }

    public List<User> getCommonsFriends(long id, long friendId) {
        List<User> commonFriendList = new ArrayList<>();
        Set<Long> userFriendList = new HashSet<>(getUser(id).getMyFriend());
        Set<Long> friendList = new HashSet<>(getUser(friendId).getMyFriend());

        if (userFriendList.isEmpty() || friendList.isEmpty()) {
            return commonFriendList;
        }

        userFriendList.retainAll(friendList);
        if (userFriendList.isEmpty()) {
            return commonFriendList;
        }

        return getFriendList(userFriendList);
    }

    public User getUser(long id) {
        Optional<User> us = storage.getAll()
                .stream()
                .filter(user -> user.getId() == id)
                .findFirst();

        if (us.isPresent()) {

            return us.get();
        } else {

            throw new NotFoundException(String.format("Ползователь c Id = %s не найден", id));
        }

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

        getUser(user.getId());

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
        if (user.getId() == 0) user.setId(++counter);
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        if (user.getMyFriend() == null) user.setMyFriend(new TreeSet<>());
    }
}
