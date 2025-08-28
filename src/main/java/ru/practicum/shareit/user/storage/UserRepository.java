package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    User update(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void deleteById(Long id);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
