package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User fromDto(UserDto dto) {
        if (dto == null) return null;
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}
