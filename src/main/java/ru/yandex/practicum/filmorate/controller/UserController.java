package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    private long counter = 0;


    @PostMapping
    public User addFilm(@Valid @RequestBody User user) {

        modificate(user);

        users.put(user.getId(), user);

        log.debug("Пользователь добавлен " + user);

        return user;
    }

    private void modificate(User user) {
        if (user.getId() == 0) user.setId(++counter);
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
    }

    @PutMapping
    public User updateFilm(@Valid @RequestBody User user) {

        if (users.containsKey(user.getId())) {

            modificate(user);

            users.put(user.getId(), user);

            log.debug("Пользователь обновлён " + user);

            return user;

        } else {
            throw new NotFoundException("Пользователя не сущетсвует с id = " + user.getId());
        }
    }


    @GetMapping
    public List<User> getFilm() {
        log.info("Кол-во пользователей {}", users.size());
        return new ArrayList<>(users.values());
    }


}
