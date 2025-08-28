package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.error.DuplicateEmailException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    @Override
    public UserDto create(UserDto dto) {
        if (repo.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Пользователь с email " + dto.getEmail() + " уже существует");
        }
        User saved = repo.save(UserMapper.fromDto(dto));
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto update(Long userId, UserDto dto) {
        User user = repo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден"));
        if (StringUtils.hasText(dto.getName())) {
            user.setName(dto.getName());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            repo.findByEmail(dto.getEmail())
                    .filter(u -> !u.getId().equals(userId))
                    .ifPresent(u -> {
                        throw new DuplicateEmailException("Email уже используется: " + dto.getEmail());
                    });
            user.setEmail(dto.getEmail());
        }
        repo.update(user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto getById(Long id) {
        return repo.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден"));
    }

    @Override
    public List<UserDto> getAll() {
        return repo.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
