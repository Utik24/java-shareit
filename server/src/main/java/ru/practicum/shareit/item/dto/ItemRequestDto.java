package ru.practicum.shareit.item.dto;

import lombok.Builder;

@Builder
public record ItemRequestDto(Long itemId, String name, Long ownerId) {
}

