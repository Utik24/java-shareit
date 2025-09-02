package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;

public record UserPatchDto(Long id, String name, @Email(message = "Email имеет неверный формат.") String email) {
}
