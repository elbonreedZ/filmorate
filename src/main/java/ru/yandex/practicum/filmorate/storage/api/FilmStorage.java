package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    void delete(long id);

    List<Film> getAll();

    Optional<Film> getById(long id);
}
