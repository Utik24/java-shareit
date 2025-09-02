package ru.practicum.shareit.request.dto;

import lombok.Builder;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.sql.Timestamp;
import java.util.List;

@Builder
public record RequestWithItems(Long id, String description, Timestamp created, List<ItemRequestDto> items) {
}
