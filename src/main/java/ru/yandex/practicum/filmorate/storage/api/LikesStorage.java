package ru.yandex.practicum.filmorate.storage.api;

import java.util.Set;

public interface LikesStorage {
    void like(long filmId, long userId);

    Set<Long> getFilmLikes(long filmId);

    void deleteAllFilmLikes(long filmId);

    void deleteLike(long filmId, long userId);
}
