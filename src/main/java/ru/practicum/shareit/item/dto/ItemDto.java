package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название обязательно")
    private String name;

    @NotBlank(message = "Описание обязательно")
    private String description;

    @NotNull(message = "Доступность обязательна")
    private Boolean available;

    private Long requestId;
}
