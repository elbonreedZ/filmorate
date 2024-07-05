package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int idCounter;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Map<Integer, User> getAll() {
        log.info("Возврат списка всех пользователей");
        return users;
    }

    @Override
    public User add(User user) {
        String login = user.getLogin();
        if (login.contains(" ")) {
            log.error("Логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пустое, использование логина для инициализации");
            user.setName(login);
        }
        log.info("Валидация прошла успешно");

        log.debug("Установка id");
        user.setId(getNextId());

        log.trace("Создание списка друзей");
        user.setFriends(new HashSet<>());

        log.debug("Добавление пользователя");
        users.put(user.getId(), user);

        log.info("Пользователь успешно добавлен: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());
        log.trace("Проверка id");
        if (oldUser == null) {
            log.error("Пользователь с указанным айди не существует");
            throw new NotFoundException("Пользователь с указанным id не существует");
        }
        String login = user.getLogin();
        if (login.contains(" ")) {
            log.error("Логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя == null, использование логина для инициализации");
            user.setName(login);
        }
        if (user.getFriends() == null) {
            log.debug("Друзья не указаны, установка старого значения");
            user.setFriends(oldUser.getFriends());
        }
        log.info("Валидация прошла успешно");

        log.debug("Обновление данных пользователя");
        users.put(user.getId(), user);

        log.info("Данные успешно обновлены: {}", user);
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
