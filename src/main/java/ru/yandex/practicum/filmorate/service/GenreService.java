package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.api.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenresDbStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<GenreDto> getAllGenres() {
        return genreStorage.getAllGenres().stream().map(GenreMapper::mapToGenreDto).collect(Collectors.toList());
    }

    public GenreDto getById(long id) {
        Optional<Genre> genre = genreStorage.getGenreById(id);
        if (genre.isEmpty()) {
            log.error("Жанр не найден id = {}", id);
            throw new NotFoundException("Жанр не найден");
        }
        return GenreMapper.mapToGenreDto(genre.get());
    }

    public void addFilmGenre(long filmId, long genreId ) {
        genreStorage.addFilmGenre(filmId, genreId);
    }

    public void deleteFilmGenres(long filmId) {
        genreStorage.deleteAllFilmGenres(filmId);
    }
}
