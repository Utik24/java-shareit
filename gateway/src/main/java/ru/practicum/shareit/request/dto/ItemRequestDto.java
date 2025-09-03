package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record ItemRequestDto(Long id,
                             @NotBlank(message = "Описание должно быть заполнено.") String description,
                             Long requestor,
                             Timestamp created) {
}
