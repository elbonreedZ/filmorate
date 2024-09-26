package ru.yandex.practicum.filmorate.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dto.ResponseUserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static ResponseUserDto mapToResponseUserDto(User user) {
        ResponseUserDto dto = new ResponseUserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        dto.setBirthday(user.getBirthday());
        return dto;
    }

    public static User mapUpdateDtoToUser(UpdateUserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setLogin(dto.getLogin());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setBirthday(dto.getBirthday());
        return user;
    }

    public static User mapCreateDtoToUser(CreateUserDto dto) {
        User user = new User();
        user.setLogin(dto.getLogin());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setBirthday(dto.getBirthday());
        return user;
    }

}