package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return service.addUser(user);
    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);
    }


    @GetMapping
    public List<User> getUsers() {
        return service.getUsers();
    }

    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable Long id) {
        return service.getUser(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.addFriend(id, friendId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.deleteFriend(id, friendId);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getUsers(@PathVariable Long id) {
        return service.getFriends(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getUsers(@PathVariable Long id, @PathVariable Long otherId) {
        return service.getCommonsFriends(id, otherId);
    }


}
