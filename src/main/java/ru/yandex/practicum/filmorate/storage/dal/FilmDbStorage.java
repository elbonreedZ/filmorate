package ru.yandex.practicum.filmorate.storage.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.RatingService;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.List;
import java.util.Optional;

@Qualifier("filmDbStorage")
@Repository
@Slf4j
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private final RatingService ratingService;
    private final GenreService genreService;
    private static final String GET_BY_ID_QUERY = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name AS rating_name, " +
            "GROUP_CONCAT(DISTINCT fg.genre_id) AS genre_ids, " +
            "GROUP_CONCAT(DISTINCT l.user_id) AS like_user_ids " +
            "FROM films f " +
            "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN likes l ON f.id = l.film_id " +
            "LEFT JOIN rating r ON f.rating_id = r.id " +
            "WHERE f.id = ? " +
            "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id";

    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";

    private static final String GET_ALL_QUERY = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name AS rating_name, " +
            "GROUP_CONCAT(DISTINCT fg.genre_id) AS genre_ids, " +
            "GROUP_CONCAT(DISTINCT l.user_id) AS like_user_ids " +
            "FROM films f " +
            "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN likes l ON f.id = l.film_id " +
            "LEFT JOIN rating r ON f.rating_id = r.id " +
            "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id";


    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, LikesDbStorage likesDbStorage, GenresDbStorage genresDbStorage, RatingDbStorage ratingDbStorage, RatingService ratingService, GenreService genreService) {
        super(jdbc, mapper);
        this.ratingService = ratingService;
        this.genreService = genreService;
    }

    @Override
    public Film add(Film film) {
        try {
            ratingService.getById(film.getMpa().getId());
        } catch (NotFoundException e) {
            throw new ValidationException("Рейтинг с id " + film.getMpa().getId() + " не существует");
        }
        for (Genre genre : film.getGenres()) {
            try {
                genreService.getById(genre.getId());
            } catch (NotFoundException e) {
                throw new ValidationException("Жанр с id " + genre.getId() + " не существует");
            }
        }
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        for (Genre genre : film.getGenres()) {
            genreService.addFilmGenre(film.getId(), genre.getId());
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        long id = film.getId();
        try {
            ratingService.getById(film.getMpa().getId());
        } catch (NotFoundException e) {
            throw new ValidationException("Рейтинг с id " + film.getMpa().getId() + " не существует");
        }
        for (Genre genre : film.getGenres()) {
            try {
                genreService.getById(genre.getId());
            } catch (NotFoundException e) {
                throw new ValidationException("Жанр с id " + genre.getId() + " не существует");
            }
        }
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                id
        );

        genreService.deleteFilmGenres(id);
        for (Genre genre : film.getGenres()) {
            genreService.addFilmGenre(id, genre.getId());
        }
        return film;
    }

    @Override
    public void delete(long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public List<Film> getAll() {
        return getMany(GET_ALL_QUERY);
    }

    @Override
    public Optional<Film> getById(long id) {
        return getOne(GET_BY_ID_QUERY, id);
    }
}
