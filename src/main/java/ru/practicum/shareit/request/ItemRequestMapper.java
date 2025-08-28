package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public final class ItemRequestMapper {
    private ItemRequestMapper() {
    }

    public static ItemRequestDto toDto(ItemRequest request) {
        if (request == null) return null;
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestorId(request.getRequestor() != null ? request.getRequestor().getId() : null)
                .created(request.getCreated())
                .build();
    }

    public static ItemRequest fromDto(ItemRequestDto dto, User requestor) {
        if (dto == null) return null;
        return ItemRequest.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .requestor(requestor)
                .created(dto.getCreated() != null ? dto.getCreated() : LocalDateTime.now())
                .build();
    }
}
