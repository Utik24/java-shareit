package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public final class BookingMapper {
    private BookingMapper() {
    }

    public static BookingDto toDto(Booking b) {
        if (b == null) return null;
        BookingDto.BookerShortDto bookerShort = b.getBooker() == null ? null :
                BookingDto.BookerShortDto.builder().id(b.getBooker().getId()).build();
        BookingDto.ItemShortDto itemShort = b.getItem() == null ? null :
                BookingDto.ItemShortDto.builder().id(b.getItem().getId()).name(b.getItem().getName()).build();
        return BookingDto.builder()
                .id(b.getId())
                .start(b.getStart())
                .end(b.getEnd())
                .status(b.getStatus())
                .itemId(b.getItem() != null ? b.getItem().getId() : null)
                .bookerId(b.getBooker() != null ? b.getBooker().getId() : null)
                .booker(bookerShort)
                .item(itemShort)
                .build();
    }

    public static Booking fromDto(BookingDto dto, Item item, User booker) {
        if (dto == null) return null;
        return Booking.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(dto.getStatus())
                .build();
    }
}