package ru.practicum.shareit.booking.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("mem")
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<Long, Booking> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null) booking.setId(seq.incrementAndGet());
        storage.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Booking update(Booking booking) {
        Objects.requireNonNull(booking.getId(), "id required");
        storage.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Booking> findAll() {
        return sorted(storage.values());
    }

    @Override
    public List<Booking> findByBookerId(Long bookerId) {
        return sorted(storage.values().stream()
                .filter(b -> b.getBooker() != null && Objects.equals(b.getBooker().getId(), bookerId))
                .collect(Collectors.toList()));
    }

    @Override
    public List<Booking> findByOwnerId(Long ownerId) {
        return sorted(storage.values().stream()
                .filter(b -> isOwner(b, ownerId))
                .collect(Collectors.toList()));
    }

    @Override
    public List<Booking> findCurrentForBooker(Long userId, LocalDateTime now) {
        return sorted(findByBookerId(userId).stream()
                .filter(b -> nonNullTimes(b) && !b.getStart().isAfter(now) && !b.getEnd().isBefore(now))
                .collect(Collectors.toList()));
    }

    @Override
    public List<Booking> findPastForBooker(Long userId, LocalDateTime now) {
        return sorted(findByBookerId(userId).stream()
                .filter(b -> nonNullTimes(b) && b.getEnd().isBefore(now))
                .collect(Collectors.toList()));
    }

    @Override
    public List<Booking> findFutureForBooker(Long userId, LocalDateTime now) {
        return sorted(findByBookerId(userId).stream()
                .filter(b -> nonNullTimes(b) && b.getStart().isAfter(now))
                .collect(Collectors.toList()));
    }

    @Override
    public List<Booking> findByBookerAndStatus(Long userId, BookingStatus status) {
        return sorted(findByBookerId(userId).stream()
                .filter(b -> b.getStatus() == status)
                .collect(Collectors.toList()));
    }

    @Override
    public List<Booking> findCurrentForOwner(Long ownerId, LocalDateTime now) {
        return sorted(findByOwnerId(ownerId).stream()
                .filter(b -> nonNullTimes(b) && !b.getStart().isAfter(now) && !b.getEnd().isBefore(now))
                .collect(Collectors.toList()));
    }

    @Override
    public List<Booking> findPastForOwner(Long ownerId, LocalDateTime now) {
        return sorted(findByOwnerId(ownerId).stream()
                .filter(b -> nonNullTimes(b) && b.getEnd().isBefore(now))
                .collect(Collectors.toList()));
    }

    @Override
    public List<Booking> findFutureForOwner(Long ownerId, LocalDateTime now) {
        return sorted(findByOwnerId(ownerId).stream()
                .filter(b -> nonNullTimes(b) && b.getStart().isAfter(now))
                .collect(Collectors.toList()));
    }

    @Override
    public List<Booking> findByOwnerAndStatus(Long ownerId, BookingStatus status) {
        return sorted(findByOwnerId(ownerId).stream()
                .filter(b -> b.getStatus() == status)
                .collect(Collectors.toList()));
    }

    @Override
    public boolean existsByBookerFinishedApproved(Long userId, Long itemId, LocalDateTime now) {
        return storage.values().stream().anyMatch(b ->
                b.getBooker() != null && Objects.equals(b.getBooker().getId(), userId) &&
                        b.getItem() != null && Objects.equals(b.getItem().getId(), itemId) &&
                        b.getStatus() == BookingStatus.APPROVED &&
                        nonNullTimes(b) && b.getEnd().isBefore(now)
        );
    }

    private boolean isOwner(Booking b, Long ownerId) {
        return b.getItem() != null && b.getItem().getOwner() != null
                && Objects.equals(b.getItem().getOwner().getId(), ownerId);
    }

    private boolean nonNullTimes(Booking b) {
        return b.getStart() != null && b.getEnd() != null;
    }

    private List<Booking> sorted(Collection<Booking> bookings) {
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());
    }
}