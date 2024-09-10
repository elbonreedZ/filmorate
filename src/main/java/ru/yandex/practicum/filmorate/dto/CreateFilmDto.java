package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class CreateFilmDto {
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Длина описания не может превышать 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность не может быть меньше или равна нулю")
    private Long duration;
    private final Set<Genre> genres = new HashSet<>();
    private Rating mpa;
}
