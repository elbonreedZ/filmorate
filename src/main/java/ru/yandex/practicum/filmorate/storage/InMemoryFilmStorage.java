package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int idCounter;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film add(Film film) {
        film.setId(getNextId());
        film.setLikes(new HashSet<>());
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
        film.setLikes(oldFilm.getLikes());
        films.put(film.getId(), film);
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
