package ru.practicum.shareit.booking.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("db")
@RequiredArgsConstructor
public class JpaBookingRepository implements BookingRepository {

    private final SpringDataBookingJpa jpa;

    @Override
    public Booking save(Booking booking) {
        return jpa.save(booking);
    }

    @Override
    public Booking update(Booking booking) {
        return jpa.save(booking);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Booking> findAll() {
        return jpa.findAll();
    }

    @Override
    public List<Booking> findByBookerId(Long bookerId) {
        return jpa.findByBooker_IdOrderByStartDesc(bookerId);
    }

    @Override
    public List<Booking> findByOwnerId(Long ownerId) {
        return jpa.findByItem_Owner_IdOrderByStartDesc(ownerId);
    }

    @Override
    public List<Booking> findCurrentForBooker(Long userId, LocalDateTime now) {
        return jpa.findCurrentForBooker(userId, now);
    }

    @Override
    public List<Booking> findPastForBooker(Long userId, LocalDateTime now) {
        return jpa.findPastForBooker(userId, now);
    }

    @Override
    public List<Booking> findFutureForBooker(Long userId, LocalDateTime now) {
        return jpa.findFutureForBooker(userId, now);
    }

    @Override
    public List<Booking> findByBookerAndStatus(Long userId, BookingStatus status) {
        return jpa.findByBooker_IdAndStatusOrderByStartDesc(userId, status);
    }

    @Override
    public List<Booking> findCurrentForOwner(Long ownerId, LocalDateTime now) {
        return jpa.findCurrentForOwner(ownerId, now);
    }

    @Override
    public List<Booking> findPastForOwner(Long ownerId, LocalDateTime now) {
        return jpa.findPastForOwner(ownerId, now);
    }

    @Override
    public List<Booking> findFutureForOwner(Long ownerId, LocalDateTime now) {
        return jpa.findFutureForOwner(ownerId, now);
    }

    @Override
    public List<Booking> findByOwnerAndStatus(Long ownerId, BookingStatus status) {
        return jpa.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, status);
    }

    @Override
    public boolean existsByBookerFinishedApproved(Long userId, Long itemId, LocalDateTime now) {
        return jpa.existsFinishedApproved(userId, itemId, now);
    }
}