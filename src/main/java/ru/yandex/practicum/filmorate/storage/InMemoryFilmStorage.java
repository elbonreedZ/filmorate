package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate FOUNDATION_OF_FILMS = LocalDate.of(1895, 12, 28);
    private int idCounter;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Map<Integer, Film> getAll() {
        return films;
    }

    @Override
    public Film add(Film film) {
        if (film.getReleaseDate().isBefore(FOUNDATION_OF_FILMS)) {
            log.error("Некорректнвя дата релиза фильма: {}", film.getReleaseDate());
            throw new ValidationException("Некорректная дата релиза");
        }

        log.info("Валидация фильма прошла успешно");

        log.trace("Создание списка лайков");
        film.setLikes(new HashSet<>());

        log.debug("Установка ID для фильма");
        film.setId(getNextId());

        log.debug("Сохранение фильма");
        films.put(film.getId(), film);

        log.info("Фильм успешно добавлен: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        Film oldFilm = films.get(film.getId());
        if (oldFilm == null) {
            log.error("Фильм с id = {} не найден", film.getId());
            throw new NotFoundException(String.format("Фильм с id = %d не найден", film.getId()));
        }
        if (film.getReleaseDate().isBefore(FOUNDATION_OF_FILMS)) {
            log.error("Некорректнвя дата релиза фильма: {}", film.getReleaseDate());
            throw new ValidationException("Некорректная дата релиза");
        }
        if (film.getLikes() == null) {
            log.debug("Лайки не указаны, установка старого значения");
            film.setLikes(oldFilm.getLikes());
        }
        log.info("Валидация фильма прошла успешно");

        log.debug("Обновление фильма");
        films.put(film.getId(), film);

        log.info("Фильм успешно обновлён: {}", film);
        return film;
    }

    @Override
    public Film delete(int id) {
        Film film = films.get(id);
        if (film == null) {
            log.error("Фильм с id = {} не найден", id);
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }
        films.remove(id);
        log.info("Фильм с id = {} успешно удалён", id);
        return film;
    }

    @Override
    public Film getById(int id) {
        return films.get(id);
    }

    private int getNextId() {
        return ++idCounter;
    }
}
