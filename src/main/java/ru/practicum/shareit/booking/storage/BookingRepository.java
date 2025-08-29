package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);

    Booking update(Booking booking);

    Optional<Booking> findById(Long id);

    List<Booking> findAll();

    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByOwnerId(Long ownerId);

    List<Booking> findCurrentForBooker(Long userId, LocalDateTime now);

    List<Booking> findPastForBooker(Long userId, LocalDateTime now);

    List<Booking> findFutureForBooker(Long userId, LocalDateTime now);

    List<Booking> findByBookerAndStatus(Long userId, BookingStatus status);

    List<Booking> findCurrentForOwner(Long ownerId, LocalDateTime now);

    List<Booking> findPastForOwner(Long ownerId, LocalDateTime now);

    List<Booking> findFutureForOwner(Long ownerId, LocalDateTime now);

    List<Booking> findByOwnerAndStatus(Long ownerId, BookingStatus status);

    boolean existsByBookerFinishedApproved(Long userId, Long itemId, LocalDateTime now);
}