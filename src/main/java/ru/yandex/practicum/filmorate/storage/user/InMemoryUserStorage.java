package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

        @Override
    public void addUserInStorage(User user) {
        users.put(user.getId(), user);

    }

    @Override
    public void updateUserInStorage(User user) {
        users.put(user.getId(), user);

    }

    @Override
    public List<User> getUserFromStorage() {
        return new ArrayList<>(users.values());
    }


}
