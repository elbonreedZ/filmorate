package ru.yandex.practicum.filmorate.storage.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;


@Slf4j
@Repository
public class GenresDbStorage extends BaseDbStorage<Genre> {
    private static final String GET_GENRES_OF_FILM_QUERY = "SELECT g.id, g.name " +
            "FROM genre g " +
            "JOIN film_genres fg ON g.id = fg.genre_id " +
            "WHERE fg.film_id = ?";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    private static final String GET_ALL_GENRES = "SELECT * FROM genre";
    private static final String GET_GENRE_BY_ID = "SELECT * FROM genre WHERE id = ?";

    public GenresDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Set<Genre> getGenresOfFilm(long filmId) {
        return new HashSet<>(getMany(GET_GENRES_OF_FILM_QUERY, filmId));
    }

    public void addFilmGenre(long filmId, long genreId) {
        jdbc.update(INSERT_FILM_GENRE_QUERY, filmId, genreId);
    }

    public void deleteAllFilmGenres(long filmId) {
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);
    }

    public List<Genre> getAllGenres() {
        return getMany(GET_ALL_GENRES);
    }

    public Optional<Genre> getGenreById(long id) {
        return getOne(GET_GENRE_BY_ID, id);
    }
}
