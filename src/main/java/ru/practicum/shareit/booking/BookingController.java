package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

import static ru.practicum.shareit.HasUserHeader.USER_HEADER;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestHeader(USER_HEADER) Long userId, @Valid @RequestBody BookingDto dto) {
        return service.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(USER_HEADER) Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam boolean approved) {
        return service.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long bookingId) {
        return service.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getByBooker(@RequestHeader(USER_HEADER) Long userId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        BookingState stateParam = BookingState.from(state);
        return service.getByBooker(userId, stateParam);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestHeader(USER_HEADER) Long ownerId,
                                       @RequestParam(defaultValue = "ALL") String state) {
        BookingState stateParam = BookingState.from(state);
        return service.getByOwner(ownerId, stateParam);
    }
}