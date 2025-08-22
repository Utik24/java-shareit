package ru.practicum.shareit.booking.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<Long, Booking> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public Booking save(Booking booking) {
        long id = seq.incrementAndGet();
        booking.setId(id);
        storage.put(id, booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Booking> findByBookerId(Long bookerId) {
        return storage.values().stream()
                .filter(b -> b.getBooker() != null && Objects.equals(b.getBooker().getId(), bookerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByOwnerId(Long ownerId) {
        return storage.values().stream()
                .filter(b -> b.getItem() != null && b.getItem().getOwner() != null
                        && Objects.equals(b.getItem().getOwner().getId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Booking update(Booking booking) {
        storage.put(booking.getId(), booking);
        return booking;
    }
}
