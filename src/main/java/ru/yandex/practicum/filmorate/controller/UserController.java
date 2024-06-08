package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private int idCounter;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAll() {
        log.info("Запрос на получение списка всех пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User add(@RequestBody @Valid User user) {
        log.info("Запрос на добавление пользователя: {}", user);

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

        log.debug("Добавление пользователя");
        users.put(user.getId(), user);

        log.info("Пользователь успешно добавлен: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        log.info("Запрос на обновление пользователя: {}", user);

        log.trace("Проверка id");
        if (users.get(user.getId()) == null) {
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
        log.info("Валидация прошла успешно");

        log.debug("Обновление данных пользователя");
        users.put(user.getId(), user);

        log.info("Данные успешно обновлены: {}", user);
        return user;
    }

    private int getNextId() {
        return ++idCounter;
    }
}
