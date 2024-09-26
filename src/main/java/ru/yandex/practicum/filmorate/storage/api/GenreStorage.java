package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Set<Genre> getGenresOfFilm(long filmId);

    void addFilmGenre(long filmId, long genreId);

    void deleteAllFilmGenres(long filmId);

    List<Genre> getAllGenres();

    Optional<Genre> getGenreById(long id);
}
