package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    public Film like(int id, int userId) {
        log.trace("Получение фильма по id");
        Film film = filmStorage.getById(id);
        if (film == null) {
            log.error("Фильм с id = {} не найден", id);
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }
        log.trace("Получение списка лайков");
        Set<Integer> likes = film.getLikes();
        log.trace("Добавление лайка в список");
        likes.add(userId);
        log.trace("Обновление списка");
        film.setLikes(likes);
        log.trace("Обновление фильма");
        filmStorage.update(film);
        log.info("Лайк успешно поставлен");
        return film;
    }

    public Film deleteLike(int id, int userId) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            log.error("Фильм с id = {} не найден", id);
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }
        log.trace("Получение списка лайков");
        Set<Integer> likes = film.getLikes();
        log.trace("Удаление лайка из списка");
        likes.remove(userId);
        log.trace("Обновление списка");
        film.setLikes(likes);
        log.trace("Обновление фильма");
        filmStorage.update(film);
        log.info("Лайк успешно удалён");
        return film;
    }

    public List<Film> getMostPopular(int count) {
        log.info("Возврат списка самых популярных фильмов");
        return getAll().stream()
                .filter(film -> !film.getLikes().isEmpty())
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film delete(int id) {
        return filmStorage.delete(id);
    }

    public List<Film> getAll() {
        log.info("Возврат списка всех фильмов");
        return new ArrayList<>(filmStorage.getAll().values());
    }

    public Film getById(int id) {
        return filmStorage.getById(id);
    }
}
