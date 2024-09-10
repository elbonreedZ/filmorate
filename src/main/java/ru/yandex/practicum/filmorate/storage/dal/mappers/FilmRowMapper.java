package ru.yandex.practicum.filmorate.storage.dal.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.dal.GenresDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.RatingDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final GenresDbStorage genresDbStorage;
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getLong("duration"));
        long ratingId = resultSet.getLong("rating_id");
        String ratingName = resultSet.getString("rating_name");
        film.setMpa(new Rating(ratingId, ratingName));
        String genreIds = resultSet.getString("genre_ids");
        if (genreIds != null) {
            for (String genreId : genreIds.split(",")) {
                film.getGenres().add(genresDbStorage.getGenreById(Long.parseLong(genreId)).orElse(null));
            }
        }
        String likeUserIds = resultSet.getString("like_user_ids");
        if (likeUserIds != null) {
            for (String likeUserId : likeUserIds.split(",")) {
                film.getLikes().add(Long.parseLong(likeUserId));
            }
        }
        return film;
    }
}