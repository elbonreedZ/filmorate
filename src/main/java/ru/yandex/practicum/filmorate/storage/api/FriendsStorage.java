package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    List<User> getUserFriends(long userId);

    List<Long> getFriendsIds(long userId);

    void addFriend(long initiatorId, long targetUserId);

    void deleteFriend(long userId, long friendId);
}
