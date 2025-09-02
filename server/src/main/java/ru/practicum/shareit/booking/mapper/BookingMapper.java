package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {

    public static BookingDto toDto(Booking b) {
        return BookingDto.builder()
                .id(b.getId())
                .start(b.getStart())
                .end(b.getEnd())
                .item(ItemMapper.toDto(b.getItem()))
                .booker(UserMapper.fromDto(b.getBooker()))
                .status(b.getStatus())
                .build();
    }

    public static Booking fromDto(BookingCreateDto bookingCreateDto, User user, Item item) {
        return Booking.builder()
                .start(bookingCreateDto.start())
                .end(bookingCreateDto.end())
                .item(item)
                .booker(user)
                .build();
    }

}