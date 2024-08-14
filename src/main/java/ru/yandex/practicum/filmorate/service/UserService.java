package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FriendDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FriendMapper;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto addFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.error("Пользователь не может добавить в друзья сам себя");
            throw new IllegalArgumentException("Пользователь не может добавить в друзья сам себя");
        }
        User user = getById(userId);
        User friend = getById(friendId);
        Set<Long> friends = user.getFriends();
        Set<Long> friendsOfFriend = friend.getFriends();
        friends.add(friendId);
        friendsOfFriend.add(userId);
        user.setFriends(friends);
        friend.setFriends(friendsOfFriend);
        userStorage.update(user);
        userStorage.update(friend);
        log.info("Друг успешно добавлен: {}", user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto deleteFriend(long userId, long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        Set<Long> friends = user.getFriends();
        Set<Long> friendsOfFriend = friend.getFriends();
        friends.remove(friendId);
        friendsOfFriend.remove(userId);
        user.setFriends(friends);
        friend.setFriends(friendsOfFriend);
        userStorage.update(user);
        userStorage.update(friend);
        log.info("Друг успешно удалён: {}", user);
        return UserMapper.mapToUserDto(user);
    }

    public List<FriendDto> getAllFriends(long id) {
        User user = getById(id);
        Set<Long> friendsIds = user.getFriends();
        List<FriendDto> friends = friendsIds.stream()
                .map(FriendMapper::mapToFriendDto)
                .toList();
        log.info("Список друзей возвращён: {}", friends);
        return friends;
    }

    public List<FriendDto> getCommonFriends(long id, long otherId) {
        User user = getById(id);
        User otherUser = getById(otherId);
        Set<Long> commonFriendsIds = new HashSet<>(user.getFriends());
        commonFriendsIds.retainAll(otherUser.getFriends());
        List<FriendDto> commonFriends = commonFriendsIds.stream()
                .map(FriendMapper::mapToFriendDto)
                .toList();
        log.info("Список общих друзей возвращён: {}", commonFriends);
        return commonFriends;
    }

    public List<User> getAll() {
        log.info("Возврат списка всех пользователей: {}", userStorage.getAll());
        return userStorage.getAll();
    }

    public User getById(long id) {
        Optional<User> userOp = userStorage.getById(id);
        if (userOp.isEmpty()) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        return userOp.get();
    }

    public UserDto add(User user) {
        String login = user.getLogin();
        if (login.contains(" ")) {
            log.error("Логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пустое, использование логина для инициализации");
            user.setName(login);
        }
        return UserMapper.mapToUserDto(userStorage.add(user));
    }

    public UserDto update(User user) {
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
        return UserMapper.mapToUserDto(updated);
    }

    public void delete(long id) {
        userStorage.delete(id);
        log.info("Пользователь с id = {} успешно удалён", id);
    }
}
