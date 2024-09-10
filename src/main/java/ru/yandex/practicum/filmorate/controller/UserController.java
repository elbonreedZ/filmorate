package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.dto.FriendDto;
import ru.yandex.practicum.filmorate.dto.ResponseUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
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
    public List<ResponseUserDto> getAll() {
        log.info("Запрос на получение списка всех пользователей");
        List<ResponseUserDto> users = userService.getAll().stream()
                .map(UserMapper::mapToResponseUserDto)
                .toList();
        log.info("Возврат списка всех пользователей: {}", users);
        return users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseUserDto add(@RequestBody @Valid CreateUserDto user) {
        log.info("Запрос на добавление пользователя: {}", user);
        ResponseUserDto created = UserMapper
                .mapToResponseUserDto(userService.add(UserMapper.mapCreateDtoToUser(user)));
        log.info("Данные успешно обновлены: {}", created);
        return created;
    }

    @PutMapping
    public ResponseUserDto update(@RequestBody @Valid UpdateUserDto user) {
        log.info("Запрос на обновление пользователя: {}", user);
        ResponseUserDto updated = UserMapper
                .mapToResponseUserDto(userService.update(UserMapper.mapUpdateDtoToUser(user)));
        log.info("Данные успешно обновлены: {}", updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id) {
        log.info("Запрос на удаление пользователя: id = {}", id);
        userService.delete(id);
        log.info("Пользователь с id = {} успешно удалён", id);
    }

    @PutMapping("/{friendId}/friends/{userId}")
    public ResponseUserDto addFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Запрос на добавление в друзья: userId = {}, friendId = {}", userId, friendId);
        User user = userService.addFriend(userId, friendId);
        log.info("Друг успешно добавлен: {}", user);
        return UserMapper.mapToResponseUserDto(user);
    }

    @DeleteMapping("/{targetId}/friends/{initiatorId}")
    public ResponseUserDto deleteFriend(@PathVariable long targetId, @PathVariable long initiatorId) {
        log.info("Запрос на удаление из друзей: init = {}, target = {}", initiatorId, targetId);
        User user = userService.deleteFriend(initiatorId, targetId);
        log.info("Друг успешно удалён: {}", user);
        return UserMapper.mapToResponseUserDto(user);
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
    public ResponseUserDto getById(@PathVariable long id) {
        log.info("Запрос на получение пользователя c id: {}", id);
        ResponseUserDto userDto = UserMapper.mapToResponseUserDto(userService.getById(id));
        log.info("Пользователь получен: {}", userDto);
        return userDto;
    }
}
