package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BookingCreateDto(
        @NotNull(message = "Время начала бронирования не указано.") @FutureOrPresent LocalDateTime start,
        @NotNull(message = "Время окончания бронирования не указано.") @Future LocalDateTime end,
        @NotNull(message = "Бронируемая вещь не указана.") Long itemId) {

}
