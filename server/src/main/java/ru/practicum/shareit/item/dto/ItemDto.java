package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Value
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long owner;
    Long requestId;
    LocalDateTime lastBooking;
    LocalDateTime nextBooking;
    List<CommentDto> comments;
}
