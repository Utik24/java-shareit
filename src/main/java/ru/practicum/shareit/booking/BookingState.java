package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    @JsonCreator
    public static BookingState from(String value) {
        if (value == null) return ALL;
        try {
            return BookingState.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ru.practicum.shareit.error.ValidationException("Unknown state: " + value);
        }
    }
}
