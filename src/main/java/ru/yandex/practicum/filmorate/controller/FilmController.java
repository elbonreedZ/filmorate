package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ResponseFilmDto;
import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<ResponseFilmDto> getAll() {
        log.info("Запрос на получение списка всех фильмов");
        List<ResponseFilmDto> films = filmService.getAll().stream()
                .map(FilmMapper::mapToResponseFilmDto)
                .collect(Collectors.toList());
        log.info("Возврат списка всех фильмов: {}", films);
        return films;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseFilmDto add(@RequestBody @Valid CreateFilmDto dto) {
        log.info("Запрос на добавление фильма: {}", dto);
        Film created = filmService.add(FilmMapper.mapNewFilmToFilm(dto));
        ResponseFilmDto filmDto = FilmMapper.mapToResponseFilmDto(created);
        log.info("Фильм успешно добавлен: {}", filmDto);
        return filmDto;
    }

    @PutMapping
    public ResponseFilmDto update(@RequestBody @Valid UpdateFilmDto film) {
        log.info("Запрос на обновление фильма: {}", film);
        Film updated = filmService.update(FilmMapper.mapUpdateFilmToFilm(film));
        ResponseFilmDto filmDto = FilmMapper.mapToResponseFilmDto(updated);
        log.info("Фильм успешно обновлён: {}", filmDto);
        return filmDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        log.info("Запрос на удаление фильма: id = {}", id);
        filmService.delete(id);
        log.info("Фильм с id = {} успешно удалён", id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable long id, @PathVariable long userId) {
        log.info("Запрос на добавление лайка фильму с id = {} от пользователя с id = {}", id, userId);
        filmService.like(id, userId);
        log.info("Лайк успешно поставлен: filmId = {}, userId = {}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос на удаление лайка фильму с id = {} от пользователя с id = {}", id, userId);
        filmService.deleteLike(id, userId);
        log.info("Лайк успешно удалён: filmId = {}, userId = {}", id, userId);
    }

    @GetMapping("/popular")
    public List<ResponseFilmDto> getMostPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрос на получение {} самых популярных фильмов", count);
        List<ResponseFilmDto> mostPopular = filmService.getMostPopular(count).stream()
                .map(FilmMapper::mapToResponseFilmDto)
                .collect(Collectors.toList());
        log.info("Возврат списка самых популярных фильмов: {}", mostPopular);
        return mostPopular;
    }

    @GetMapping("/{id}")
    public ResponseFilmDto getById(@PathVariable int id) {
        log.info("Запрос на получение фильма c id: {}", id);
        ResponseFilmDto filmDto = FilmMapper.mapToResponseFilmDto(filmService.getById(id));
        log.info("Фильм получен: {}", filmDto);
        return filmDto;
    }

}
