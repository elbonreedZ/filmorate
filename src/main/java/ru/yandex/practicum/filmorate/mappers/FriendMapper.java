package ru.yandex.practicum.filmorate.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FriendDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FriendMapper {
    public static FriendDto mapToFriendDto(Long id) {
        FriendDto dto = new FriendDto();
        dto.setId(id);
        return dto;
    }
}
