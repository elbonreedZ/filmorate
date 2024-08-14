package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.RatingMapper;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dal.RatingDbStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {
    private final RatingDbStorage ratingDbStorage;

    public List<RatingDto> getAllRatings() {
        return ratingDbStorage.getAllRatings().stream().map(RatingMapper::mapToRatingDto).collect(Collectors.toList());
    }

    public RatingDto getById(long id) {
        Optional<Rating> rating = ratingDbStorage.getRatingById(id);
        if (rating.isEmpty()) {
            log.error("Рейтинг не найден id = {}", id);
            throw new NotFoundException("Рейтинг не найден");
        }
        return RatingMapper.mapToRatingDto(rating.get());
    }
}
