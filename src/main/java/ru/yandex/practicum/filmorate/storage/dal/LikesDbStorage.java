package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataException;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Repository
public class LikesDbStorage {
    private final JdbcTemplate jdbc;
    private static final String GET_FILM_LIKES_QUERY = "SELECT user_id FROM likes WHERE film_id = ?";

    private static final String INSERT_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";

    private static final String DELETE_FILM_LIKES_QUERY = "DELETE FROM likes WHERE film_id = ?";

    public void like(long filmId, long userId) {
        int rowUpdated = jdbc.update(INSERT_LIKE_QUERY, filmId, userId);
        if (rowUpdated == 0) {
            log.error("Не удалось удалить данные");
            throw new DataException("Не удалось удалить данные");
        }
    }

    public Set<Long> getFilmLikes(long filmId) {
        return new HashSet<>(jdbc.queryForList(GET_FILM_LIKES_QUERY, Long.class, filmId));
    }

    public void deleteAllFilmLikes(long filmId) {
        jdbc.update(DELETE_FILM_LIKES_QUERY, filmId);
    }

}
