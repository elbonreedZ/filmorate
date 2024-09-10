package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class ResponseFilmDto {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Set<GenreDto> genres = new HashSet<>();
    private RatingDto mpa;
}
