package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getAll() {
        log.info("Запрос на получение списка всех фильмов");
        return filmService.getAll();
    }

    @PostMapping
    public Film add(@RequestBody @Valid Film film) {
        log.info("Запрос на добавление фильма: {}", film);
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        log.info("Запрос на обновление фильма: {}", film);
        return filmService.update(film);
    }

    @DeleteMapping("/{id}")
    public Film delete(@PathVariable int id) {
        log.info("Запрос на удаление фильма: id = {}", id);
        return filmService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film like(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос на добавление лайка фильму с id = {} от пользователя с id = {}", id, userId);
        return filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос на удаление лайка фильму с id = {} от пользователя с id = {}", id, userId);
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрос на получение {} самых популярных фильмов", count);
        return filmService.getMostPopular(count);
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable int id) {
        log.info("Запрос на получение фильма c id: {}", id);
        return filmService.getById(id);
    }

}
