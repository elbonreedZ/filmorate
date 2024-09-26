package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.ResponseFilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.stream.Collectors;

public class FilmMapper {
    public static Film mapNewFilmToFilm(CreateFilmDto dto) {
        Film film = new Film();
        film.setDescription(dto.getDescription());
        film.setName(dto.getName());
        film.setDuration(dto.getDuration());
        film.setReleaseDate(dto.getReleaseDate());
        film.setGenres(dto.getGenres());
        film.setMpa(dto.getMpa());
        return film;
    }

    public static ResponseFilmDto mapToResponseFilmDto(Film film) {
        ResponseFilmDto dto = new ResponseFilmDto();
        dto.setId(film.getId());
        dto.setDescription(film.getDescription());
        dto.setName(film.getName());
        dto.setDuration(film.getDuration());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setGenres(film.getGenres().stream().map(GenreMapper::mapToGenreDto).collect(Collectors.toSet()));
        dto.setMpa(RatingMapper.mapToRatingDto(film.getMpa()));
        return dto;
    }

    public static Film mapUpdateFilmToFilm(UpdateFilmDto dto) {
        Film film = new Film();
        film.setId(dto.getId());
        film.setDescription(dto.getDescription());
        film.setName(dto.getName());
        film.setDuration(dto.getDuration());
        film.setReleaseDate(dto.getReleaseDate());
        film.setGenres(dto.getGenres());
        film.setMpa(dto.getMpa());
        return film;
    }

}
