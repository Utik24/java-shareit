package ru.practicum.shareit.booking.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BookingCreateDto(LocalDateTime start, LocalDateTime end, Long itemId) {

}
