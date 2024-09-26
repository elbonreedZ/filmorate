package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.api.LikesStorage;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikesStorage likesStorage;

    public void like(long filmId, long userId) {
        likesStorage.like(filmId, userId);
    }

    public Set<Long> getFilmLikes(long filmId) {
        return likesStorage.getFilmLikes(filmId);
    }

    public void deleteAllFilmLikes(long filmId) {
        likesStorage.deleteAllFilmLikes(filmId);
    }

    public void deleteLike(long filmId, long userId) {
        likesStorage.deleteLike(filmId, userId);
    }
}
