package ru.practicum.shareit.item.mapper;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.stream.Collectors;

public final class ItemMapper {
    private ItemMapper() {
    }

    public static ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner() != null ? item.getOwner().getId() : null)
                .comments(item.getComments() != null
                        ? item.getComments().stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList())
                        : null)
                .requestId(item.getRequestId())
                .build();
    }

    public static Item fromDto(ItemDto dto) {
        User owner = dto.getOwner() != null ? User.builder().id(dto.getOwner()).build() : null;

        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .requestId(dto.getRequestId())
                .build();
    }

    public static ItemRequestDto mapToItemRequestDto(Item item) {
        return ItemRequestDto.builder()
                .itemId(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }

}