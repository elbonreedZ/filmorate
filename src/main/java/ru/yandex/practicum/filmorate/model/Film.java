package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Длина описания не может превышать 200 символов")
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
}
