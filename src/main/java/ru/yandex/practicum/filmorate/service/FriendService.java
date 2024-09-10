package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.FriendsStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FriendService {
    private final FriendsStorage friendsStorage;

    public List<User> getUserFriends(long userId) {
        return friendsStorage.getUserFriends(userId);
    }

    public List<Long> getFriendsIds(long userId) {
        return friendsStorage.getFriendsIds(userId);
    }

    public void addFriend(long initiatorId, long targetUserId) {
        friendsStorage.addFriend(initiatorId, targetUserId);
    }


    public void deleteFriend(long userId, long friendId) {
        friendsStorage.deleteFriend(userId, friendId);
    }
}
