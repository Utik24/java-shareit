package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    @Test
    void toDto_mapsAllFields() {
        User user = User.builder().id(1L).name("user").email("user@mail.com").build();
        Item item = Item.builder()
                .id(2L)
                .name("item")
                .description("desc")
                .available(true)
                .owner(user)
                .build();
        Booking booking = Booking.builder()
                .id(3L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        BookingDto dto = BookingMapper.toDto(booking);

        assertEquals(3L, dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(item.getId(), dto.getItem().getId());
        assertEquals(user.getId(), dto.getBooker().getId());
        assertEquals(BookingStatus.WAITING, dto.getStatus());
    }

    @Test
    void fromDto_mapsFields() {
        User user = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(2L)
                .name("item")
                .description("desc")
                .available(true)
                .owner(user)
                .build();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        BookingCreateDto createDto = BookingCreateDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .build();

        Booking booking = BookingMapper.fromDto(createDto, user, item);

        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(user, booking.getBooker());
    }
}