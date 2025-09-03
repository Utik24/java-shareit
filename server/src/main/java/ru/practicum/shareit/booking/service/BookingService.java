package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.List;

public interface BookingService {

    BookingDto create(long userId, BookingCreateDto bookingCreateDto);

    BookingDto approve(long userId, long bookingId, boolean approved);

    BookingDto getById(long userId, long bookingId);

    List<BookingDto> getByBooker(Long userId, String state);

    List<BookingDto> getByOwner(Long userId, String state);

    List<Booking> findAllByItemIdIn(Collection<Long> itemIds);

    Booking findByBookerIdAndItemId(Long userId, Long itemId);
}

