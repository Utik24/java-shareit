package ru.practicum.shareit.request.dto;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record RequestDto(Long id, String description, Long requestor, Timestamp created) {
}
