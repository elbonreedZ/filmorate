package ru.yandex.practicum.filmorate.storage.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendService;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Qualifier("userDbStorage")
@Slf4j
@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private final FriendService friendService;

    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";

    private static final String GET_ALL_QUERY = "SELECT * FROM users";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper, FriendService friendService) {
        super(jdbc, mapper);
        this.friendService = friendService;
    }

    @Override
    public User add(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        List<Long> existingFriendIds =  friendService.getFriendsIds(user.getId());

        List<Long> friendsToAdd = user.getFriends().stream()
                .filter(friendId -> !existingFriendIds.contains(friendId))
                .toList();

        for (long friendId : friendsToAdd) {
            friendService.addFriend(user.getId(), friendId);
        }

        List<Long> friendsToRemove = existingFriendIds.stream()
                .filter(friendId -> !user.getFriends().contains(friendId))
                .toList();

        for (long friendId : friendsToRemove) {
            friendService.deleteFriend(user.getId(), friendId);
        }
        return user;
    }

    @Override
    public void delete(long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public List<User> getAll() {
        return getMany(GET_ALL_QUERY);
    }

    @Override
    public Optional<User> getById(long id) {
        Optional<User> userOpt = getOne(GET_BY_ID_QUERY, id);
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            user.setFriends(new HashSet<>(friendService.getFriendsIds(id)));
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
