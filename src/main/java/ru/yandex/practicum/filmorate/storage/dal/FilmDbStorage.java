package ru.yandex.practicum.filmorate.storage.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.List;
import java.util.Optional;

@Qualifier("filmDbStorage")
@Repository
@Slf4j
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private final ResultSetExtractor<List<Film>> resultSetExtractor;
    private final LikesDbStorage likesDbStorage;
    private final GenresDbStorage genresDbStorage;
    private final RatingDbStorage ratingDbStorage;
    private static final String GET_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";

    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";

    private static final String GET_ALL_QUERY = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, " +
            "GROUP_CONCAT(DISTINCT fg.genre_id) AS genre_ids, " +
            "GROUP_CONCAT(DISTINCT l.user_id) AS like_user_ids " +
            "FROM films f " +
            "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN likes l ON f.id = l.film_id " +
            "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id";


    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, ResultSetExtractor<List<Film>> resultSetExtractor, LikesDbStorage likesDbStorage, GenresDbStorage genresDbStorage, RatingDbStorage ratingDbStorage) {
        super(jdbc, mapper);
        this.resultSetExtractor = resultSetExtractor;
        this.likesDbStorage = likesDbStorage;
        this.genresDbStorage = genresDbStorage;
        this.ratingDbStorage = ratingDbStorage;
    }

    @Override
    public Film add(Film film) {
        if (ratingDbStorage.getRatingById(film.getMpa().getId()).isEmpty()) {
            throw new ValidationException("Рейтинг с id " + film.getMpa().getId() + " не существует");
        }
        for (Genre genre : film.getGenres()) {
            if (genresDbStorage.getGenreById(genre.getId()).isEmpty()) {
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
            genresDbStorage.addFilmGenre(id, genre.getId());
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        long id = film.getId();
        if (ratingDbStorage.getRatingById(film.getMpa().getId()).isEmpty()) {
            throw new ValidationException("Рейтинг с id " + film.getMpa().getId() + " не существует");
        }
        for (Genre genre : film.getGenres()) {
            if (genresDbStorage.getGenreById(genre.getId()).isEmpty()) {
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

        genresDbStorage.deleteAllFilmGenres(id);

        for (Genre genre : film.getGenres()) {
            genresDbStorage.addFilmGenre(id, genre.getId());
        }

        likesDbStorage.deleteAllFilmLikes(id);

        for (Long userId : film.getLikes()) {
            likesDbStorage.like(id, userId);
        }
        return film;
    }

    @Override
    public void delete(long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public List<Film> getAll() {
        return jdbc.query(GET_ALL_QUERY, resultSetExtractor);
    }

    @Override
    public Optional<Film> getById(long id) {
        Optional<Film> filmOpt = getOne(GET_BY_ID_QUERY, id);
        Film film;
        if (filmOpt.isPresent()) {
            film = filmOpt.get();
            film.setGenres(genresDbStorage.getGenresOfFilm(id));
            film.setLikes(likesDbStorage.getFilmLikes(id));
            return Optional.of(film);
        }
        return Optional.empty();
    }
}
