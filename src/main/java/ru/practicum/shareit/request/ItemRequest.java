package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    private Long id;                   // идентификатор запроса
    private String description;        // что ищем
    private User requestor;            // автор запроса
    private LocalDateTime created;     // когда создан
}
