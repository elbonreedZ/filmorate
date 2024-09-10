package ru.yandex.practicum.filmorate.storage.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.api.RatingStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class RatingDbStorage extends BaseDbStorage<Rating> implements RatingStorage {
    private static final String GET_ALL_RATINGS = "SELECT * FROM rating";
    private static final String GET_RATING_BY_ID = "SELECT * FROM rating WHERE id = ?";

    public RatingDbStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    public List<Rating> getAllRatings() {
        return getMany(GET_ALL_RATINGS);
    }

    public Optional<Rating> getRatingById(long id) {
        return getOne(GET_RATING_BY_ID, id);
    }
}
