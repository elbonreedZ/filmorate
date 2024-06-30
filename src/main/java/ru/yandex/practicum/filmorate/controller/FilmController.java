package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final LocalDate FOUNDATION_OF_FILMS = LocalDate.of(1895, 12, 28);

    private int idCounter;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAll() {
        log.info("Запрос на получение списка всех фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film add(@RequestBody @Valid Film film) {
        log.info("Запрос на добавление фильма: {}", film);

        if (film.getReleaseDate().isBefore(FOUNDATION_OF_FILMS)) {
            log.error("Некорректнвя дата релиза фильма: {}", film.getReleaseDate());
            throw new ValidationException("Некорректная дата релиза");
        }

        log.info("Валидация фильма прошла успешно");

        log.debug("Установка ID для фильма");
        film.setId(getNextId());

        log.debug("Сохранение фильма");
        films.put(film.getId(), film);

        log.info("Фильм успешно добавлен: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        log.info("Запрос на обновление фильма: {}", film);

        if (films.get(film.getId()) == null) {
            log.error("Фильм с указанным айди не существует");
            throw new NotFoundException("Фильм с указанным айди не существует");
        }
        if (film.getReleaseDate().isBefore(FOUNDATION_OF_FILMS)) {
            log.error("Некорректнвя дата релиза фильма: {}", film.getReleaseDate());
            throw new ValidationException("Некорректная дата релиза");
        }

        log.info("Валидация фильма прошла успешно");

        log.debug("Обновление фильма");
        films.put(film.getId(), film);

        log.info("Фильм успешно обновлён: {}", film);
        return film;
    }

    private int getNextId() {
        return ++idCounter;
    }
}
