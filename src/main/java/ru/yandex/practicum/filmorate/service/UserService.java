package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public User addFriend(int userId, int friendId) {
        log.trace("Получение пользователя");
        User user = userStorage.getAll().get(userId);
        log.trace("Получение друга пользователя");
        User friend = userStorage.getAll().get(friendId);
        if (user == null) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        if (friend == null) {
            log.error("Пользователь, которого пытаются добавить в друзья с id = {} не найден", friendId);
            throw new NotFoundException(
                    String.format("Пользователь, которого пытаются добавить в друзья с id = %d не найден", friendId)
            );
        }
        if (userId == friendId) {
            log.error("Пользователь не может добавить в друзья сам себя");
            throw new IllegalArgumentException("Пользователь не может добавить в друзья сам себя");
        }
        log.trace("Получение списков друзей");
        Set<Integer> friends = user.getFriends();
        Set<Integer> friendsOfFriend = friend.getFriends();
        log.trace("Добавление друга в списоки");
        friends.add(friendId);
        friendsOfFriend.add(userId);
        log.trace("Установка обновленных списков");
        user.setFriends(friends);
        friend.setFriends(friendsOfFriend);
        log.trace("Обновление пользователя");
        userStorage.update(user);
        log.trace("Обновление друга");
        userStorage.update(friend);
        log.info("Друг успешно добавлен");
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        log.trace("Получение пользователя");
        User user = userStorage.getById(userId);
        log.trace("Получение друга пользователя");
        User friend = userStorage.getById(friendId);
        if (user == null) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        if (friend == null) {
            log.error("Пользователь, которого пытаются удалить из друзей с id = {} не найден", friendId);
            throw new NotFoundException(
                    String.format("Пользователь, которого пытаются удалить из друзей = %d не найден", friendId)
            );
        }

        log.trace("Получение списков друзей");
        Set<Integer> friends = user.getFriends();
        Set<Integer> friendsOfFriend = friend.getFriends();
        /*if (!friends.contains(friendId)) {
            log.error("Пользователь, которого пытаются удалить из друзей с id = {} не найден в списке друзей", friendId);
            throw new NotFoundException(String.format(
                    "Пользователь, которого пытаются удалить из друзей с id = %d не найден в списке друзей", friendId)
            );
        }*/
        log.trace("Удаление друга");
        friends.remove(friendId);
        friendsOfFriend.remove(userId);
        log.trace("Обновление спискoв друзей");
        user.setFriends(friends);
        friend.setFriends(friendsOfFriend);
        log.trace("Обновление пользователей");
        userStorage.update(user);
        userStorage.update(friend);
        log.info("Друг успешно удалён");
        return user;
    }

    public List<User> getAllFriends(int id) {
        log.trace("Получение списка друзей пользователя");
        User user = userStorage.getById(id);
        if (user == null) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        Set<Integer> friendsIds = user.getFriends();
        log.trace("Преобразование списка индексов в cписок пользователей");
        log.info("Список друзей пользователя возвращён");
        return friendsIds.stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        log.trace("Получение пользователя");
        User user = userStorage.getById(id);
        if (user == null) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        log.trace("Получение другого пользователя");
        User otherUser = userStorage.getById(otherId);
        if (otherUser == null) {
            log.error("Пользователь с id = {} не найден", otherId);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", otherId));
        }
        log.trace("Cоздание списка общих друзей");
        Set<Integer> commonFriends = new HashSet<>(user.getFriends());
        log.trace("Объединение друг списков");
        commonFriends.retainAll(otherUser.getFriends());
        log.trace("Преобразование списка индексов в список пользователей");
        log.info("Список общих друзей возвращён");
        return commonFriends.stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public List<User> getAll() {
        return new ArrayList<>(userStorage.getAll().values());
    }

    public User getById(int id) {
        return userStorage.getById(id);
    }

    public User add(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User delete(int id) {
        return userStorage.delete(id);
    }
}
