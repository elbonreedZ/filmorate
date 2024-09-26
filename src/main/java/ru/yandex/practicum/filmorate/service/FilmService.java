package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {

    private static final LocalDate FOUNDATION_OF_FILMS = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeService likeService;


    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService, LikeService likeService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.likeService = likeService;
    }

    public void like(long id, long userId) {
        userService.getById(userId);
        getById(id);
        likeService.like(id, userId);
    }

    public void deleteLike(long id, long userId) {
        userService.getById(userId);
        getById(id);
        likeService.deleteLike(id, userId);
    }

    public List<Film> getMostPopular(int count) {
        return filmStorage.getAll().stream()
                .filter(film -> !film.getLikes().isEmpty())
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    public Film add(Film film) {
        if (film.getReleaseDate().isBefore(FOUNDATION_OF_FILMS)) {
            log.error("Некорректнвя дата релиза фильма: {}", film.getReleaseDate());
            throw new ValidationException("Некорректная дата релиза");
        }
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        if (film.getReleaseDate().isBefore(FOUNDATION_OF_FILMS)) {
            log.error("Некорректнвя дата релиза фильма: {}", film.getReleaseDate());
            throw new ValidationException("Некорректная дата релиза");
        }
        return filmStorage.update(film);
    }

    public void delete(long id) {
        filmStorage.delete(id);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(long id) {
        Optional<Film> filmOpt = filmStorage.getById(id);
        if (filmOpt.isEmpty()) {
            log.error("Фильм с id = {} не найден", id);
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }
        return filmOpt.get();
    }
}
