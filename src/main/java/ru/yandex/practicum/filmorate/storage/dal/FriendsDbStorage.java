package ru.yandex.practicum.filmorate.storage.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
@Slf4j
public class FriendsDbStorage extends BaseDbStorage<User> {
    private static final String GET_ALL_FRIENDS_QUERY = "SELECT u.* FROM users u " +
            "JOIN relations r ON u.id = r.initiator_id " +
            "WHERE r.target_user_id = ? " +
            "AND r.status_id = (SELECT id FROM status_types WHERE type = 'UNCONFIRMED') " +
            "UNION " +
            "SELECT u.* FROM users u " +
            "JOIN ( " +
            "    SELECT initiator_id AS friend_id FROM relations " +
            "    WHERE target_user_id = ? " +
            "    AND status_id = (SELECT id FROM status_types WHERE type = 'CONFIRMED') " +
            "    UNION " +
            "    SELECT target_user_id AS friend_id FROM relations " +
            "    WHERE initiator_id = ? " +
            "    AND status_id = (SELECT id FROM status_types WHERE type = 'CONFIRMED') " +
            ") AS friends " +
            "ON u.id = friends.friend_id;";
    private static final String GET_USER_FRIENDS_IDS_QUERY = "SELECT u.id AS friend_id " +
            "FROM users u " +
            "JOIN relations r ON u.id = r.initiator_id " +
            "WHERE r.target_user_id = ? " +
            "AND r.status_id = (SELECT id FROM status_types WHERE type = 'UNCONFIRMED') " +

            "UNION " +

            "SELECT u.id AS friend_id " +
            "FROM users u " +
            "JOIN ( " +
            "    SELECT initiator_id AS friend_id " +
            "    FROM relations " +
            "    WHERE target_user_id = ? AND status_id = (SELECT id FROM status_types WHERE type = 'CONFIRMED') " +
            "    UNION " +
            "    SELECT target_user_id AS friend_id " +
            "    FROM relations " +
            "    WHERE initiator_id = ? AND status_id = (SELECT id FROM status_types WHERE type = 'CONFIRMED') " +
            ") AS friends ON u.id = friends.friend_id;";
    private static final String CONFIRM_QUERY = "UPDATE relations " +
            "SET status_id = (SELECT id FROM status_types WHERE type = 'CONFIRMED') " +
            "WHERE initiator_id = ? AND target_user_id = ? " +
            "AND status_id = (SELECT id FROM status_types WHERE type = 'UNCONFIRMED')";


    public FriendsDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> getUserFriends(long userId) {
        return getMany(GET_ALL_FRIENDS_QUERY, userId, userId, userId);
    }

    public List<Long> getFriendsIds(long userId) {
        return jdbc.query(GET_USER_FRIENDS_IDS_QUERY, (rs, rowNum) -> rs.getLong("friend_id"), userId, userId, userId);
    }

    public void addFriend(long initiatorId, long targetUserId) {
        String insertQuery = "INSERT INTO relations (initiator_id, target_user_id, status_id) " +
                "VALUES (?, ?, (SELECT id FROM status_types WHERE type = 'UNCONFIRMED'))";
        jdbc.update(insertQuery, initiatorId, targetUserId);
    }

    public void confirmFriendRequest(long initiatorId, long targetUserId) {
        update(CONFIRM_QUERY, initiatorId, targetUserId);
    }

    public void deleteFriend(long userId, long friendId) {
        String deleteQuery = "DELETE FROM relations WHERE initiator_id = ? AND target_user_id = ?";
        jdbc.update(deleteQuery, friendId, userId);
        //я без понятия как пройти тесты, наставник не отвечает
    }
}
