package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FriendDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        log.info("Запрос на получение списка всех пользователей");
        return userService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@RequestBody @Valid User user) {
        log.info("Запрос на добавление пользователя: {}", user);
        return userService.add(user);
    }

    @PutMapping
    public UserDto update(@RequestBody @Valid User user) {
        log.info("Запрос на обновление пользователя: {}", user);
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id) {
        log.info("Запрос на удаление пользователя: id = {}", id);
        userService.delete(id);
    }

    @PutMapping("/{friendId}/friends/{userId}")
    public UserDto addFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Запрос на добавление в друзья: userId = {}, friendId = {}", userId, friendId);
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{targetId}/friends/{initiatorId}")
    public UserDto deleteFriend(@PathVariable long targetId, @PathVariable long initiatorId) {
        log.info("Запрос на удаление из друзей: init = {}, target = {}", initiatorId, targetId);
        return userService.deleteFriend(initiatorId, targetId);
    }

    @GetMapping("/{id}/friends")
    public List<FriendDto> getFriends(@PathVariable long id) {
        log.info("Запрос на получение списка друзей: userId = {}", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<FriendDto> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Запрос на получение списка общих друзей: userId = {}, otherId = {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("Запрос на получение пользователя c id: {}", id);
        UserDto userDto = UserMapper.mapToUserDto(userService.getById(id));
        log.info("Пользователь получен: {}", userDto);
        return userDto;
    }
}
