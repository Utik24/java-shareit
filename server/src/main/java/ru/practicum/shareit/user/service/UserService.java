package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto getById(Long id);

    UserDto update(Long id, UserPatchDto userPatchDto);

    void delete(Long id);
}
