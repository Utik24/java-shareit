package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Value
public class ItemDto {
    Long id;
    @NotBlank(message = "Имя должно быть заполнено.")
    @Size(max = 50)
    String name;
    @NotBlank(message = "Описание должно быть заполнено.")
    @Size(max = 2000)
    String description;
    @NotNull
    Boolean available;
    Long owner;
    Long requestId;
    LocalDateTime lastBooking;
    LocalDateTime nextBooking;
    List<CommentDto> comments;
}
