package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int idCounter;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
        user.setId(getNextId());
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());
        if (oldUser == null) {
            log.error("Пользователь с указанным айди не существует");
            throw new NotFoundException("Пользователь с указанным id не существует");
        }
        user.setFriends(oldUser.getFriends());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(int id) {
        User user = users.get(id);
        if (user == null) {
            log.error("Пользователь с указанным айди не существует");
            throw new NotFoundException("Пользователь с указанным айди не существует");
        }
        users.remove(id);
        log.info("Пользователь с id = {} успешно удалён", id);
        return user;
    }

    @Override
    public User getById(int id) {
        return users.get(id);
    }

    private int getNextId() {
        return ++idCounter;
    }
}
