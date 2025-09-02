package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.fromDto(userRepository.save(UserMapper.fromDto(userDto)));
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + id + " не найден."));

        return UserMapper.fromDto(user);
    }

    @Override
    public UserDto update(Long id, UserPatchDto userPatchDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + id + " не найден."));

        User updatedUser = User.builder()
                .id(user.getId())
                .name(userPatchDto.name() != null ? userPatchDto.name() : user.getName())
                .email(userPatchDto.email() != null ? userPatchDto.email() : user.getEmail())
                .build();

        return UserMapper.fromDto(userRepository.save(updatedUser));
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
