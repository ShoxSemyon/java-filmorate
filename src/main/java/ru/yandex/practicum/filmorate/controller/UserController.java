package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    Map<Integer, User> users;
    int id;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    UserController() {
        users = new HashMap<>();
        id = 0;

    }

    @PostMapping
    public User addFilm(@Valid @RequestBody User user) {

        modificateIdentifier(user);

        users.put(user.getId(), modificateName(user));

        logger.debug("Пользователь добавлен " + user);

        return user;
    }

    private void modificateIdentifier(User user) {
        if (user.getId() == 0) user.setId(++id);
    }

    @PutMapping
    public User updateFilm(@Valid @RequestBody User user) {

        if (users.containsKey(user.getId())) {
            users.put(user.getId(), modificateName(user));

            logger.debug("Пользователь обновлён " + user);

            return user;

        } else {
            throw new UserNotFoundException();
        }
    }

    private User modificateName(User user) {
        if (user.getName() == null) user.setName(user.getLogin());
        return user;
    }

    @GetMapping
    public List<User> getFilm() {
        return new ArrayList<>(users.values());
    }


}
