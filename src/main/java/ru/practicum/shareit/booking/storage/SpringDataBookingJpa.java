package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface SpringDataBookingJpa extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc(Long userId);

    List<Booking> findByBooker_IdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long userId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findByItem_Owner_IdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long ownerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    boolean existsByBooker_IdAndItem_IdAndStatusAndEndBefore(Long userId, Long itemId, BookingStatus status, LocalDateTime now);

    boolean existsByItem_IdAndStatusInAndStartLessThanAndEndGreaterThan(Long itemId, Collection<BookingStatus> statuses, LocalDateTime end, LocalDateTime start);
}