package ru.yandex.practicum.filmorate.storage.dal.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dal.GenresDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.RatingDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FilmResultSetExtractor implements ResultSetExtractor<List<Film>> {
    private final RatingDbStorage ratingDbStorage;
    private final GenresDbStorage genresDbStorage;

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException {

        Map<Long, Film> films = new HashMap<>();

        while (rs.next()) {
            long filmId = rs.getLong("id");
            Film film = films.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getLong("duration"));
                long ratingId = rs.getLong("rating_id");
                film.setMpa(ratingDbStorage.getRatingById(ratingId).orElse(null));
                film.setGenres(new HashSet<>());
                film.setLikes(new HashSet<>());
                films.put(filmId, film);
            }
            String genreIds = rs.getString("genre_ids");
            if (genreIds != null) {
                for (String genreId : genreIds.split(",")) {
                    film.getGenres().add(genresDbStorage.getGenreById(Long.parseLong(genreId)).orElse(null));
                }
            }

            String likeUserIds = rs.getString("like_user_ids");
            if (likeUserIds != null) {
                for (String likeUserId : likeUserIds.split(",")) {
                    film.getLikes().add(Long.parseLong(likeUserId));
                }
            }
        }

        return new ArrayList<>(films.values());
    }
}
