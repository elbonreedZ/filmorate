package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public User addFriend(int userId, int friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
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
        Set<Integer> friends = user.getFriends();
        Set<Integer> friendsOfFriend = friend.getFriends();
        friends.add(friendId);
        friendsOfFriend.add(userId);
        user.setFriends(friends);
        friend.setFriends(friendsOfFriend);
        userStorage.update(user);
        userStorage.update(friend);
        log.info("Друг успешно добавлен: {}", user);
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        User user = userStorage.getById(userId);
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
        Set<Integer> friends = user.getFriends();
        Set<Integer> friendsOfFriend = friend.getFriends();
        friends.remove(friendId);
        friendsOfFriend.remove(userId);
        user.setFriends(friends);
        friend.setFriends(friendsOfFriend);
        userStorage.update(user);
        userStorage.update(friend);
        log.info("Друг успешно удалён: {}", user);
        return user;
    }

    public List<User> getAllFriends(int id) {
        User user = userStorage.getById(id);
        if (user == null) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        Set<Integer> friendsIds = user.getFriends();
        List<User> friends = friendsIds.stream()
                .map(userStorage::getById)
                .toList();
        log.info("Список друзей возвращён: {}", friends);
        return friends;
    }

    public List<User> getCommonFriends(int id, int otherId) {
        User user = userStorage.getById(id);
        if (user == null) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        User otherUser = userStorage.getById(otherId);
        if (otherUser == null) {
            log.error("Пользователь с id = {} не найден", otherId);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", otherId));
        }
        Set<Integer> commonFriendsIds = new HashSet<>(user.getFriends());
        commonFriendsIds.retainAll(otherUser.getFriends());
        List<User> commonFriends = commonFriendsIds.stream()
                .map(userStorage::getById)
                .toList();
        log.info("Список общих друзей возвращён: {}", commonFriends);
        return commonFriends;
    }

    public List<User> getAll() {
        log.info("Возврат списка всех пользователей: {}", userStorage.getAll());
        return userStorage.getAll();
    }

    public User getById(int id) {
        User user = userStorage.getById(id);
        if (user == null) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        log.info("Пользователь получен: {}", user);
        return user;
    }

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
        return userStorage.add(user);
    }

    public User update(User user) {
        String login = user.getLogin();
        if (login.contains(" ")) {
            log.error("Логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя == null, использование логина для инициализации");
            user.setName(login);
        }
        User updated = userStorage.update(user);
        log.info("Данные успешно обновлены: {}", updated);
        return updated;
    }

    public User delete(int id) {
        return userStorage.delete(id);
    }
}
