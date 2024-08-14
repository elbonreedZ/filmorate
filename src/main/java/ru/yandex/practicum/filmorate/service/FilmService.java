package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class FilmService {

    private static final LocalDate FOUNDATION_OF_FILMS = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film like(long id, long userId) {
        Film film = getById(id);
        Optional<User> user = userStorage.getById(userId);
        if (user.isEmpty()) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        Set<Long> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
        filmStorage.update(film);
        log.info("Лайк успешно поставлен: {}", film);
        return film;
    }

    public void deleteLike(long id, long userId) {
        Film film = getById(id);
        Optional<User> user = userStorage.getById(userId);
        if (user.isEmpty()) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        Set<Long> likes = film.getLikes();
        likes.remove(userId);
        film.setLikes(likes);
        filmStorage.update(film);
        log.info("Лайк успешно удалён: {}", film);
    }

    public List<Film> getMostPopular(int count) {
        List<Film> mostPopular = filmStorage.getAll().stream()
                .filter(film -> !film.getLikes().isEmpty())
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
        log.info("Возврат списка самых популярных фильмов: {}", mostPopular);
        return mostPopular;
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
        Film updated = filmStorage.update(film);
        log.info("Фильм успешно обновлён: {}", updated);
        return updated;
    }

    public void delete(long id) {
        filmStorage.delete(id);
        log.info("Фильм с id = {} успешно удалён", id);
    }

    public List<Film> getAll() {
        log.info("Возврат списка всех фильмов: {}", filmStorage.getAll());
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
