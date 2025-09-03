package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
                SELECT b FROM Booking b
                WHERE (:isOwner = true AND b.item.owner.id = :userId OR :isOwner = false AND b.booker.id = :userId)
                    AND (
                        :state = 'ALL' OR
                        (:state = 'CURRENT' AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP) OR
                        (:state = 'PAST' AND b.end < CURRENT_TIMESTAMP) OR
                        (:state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP) OR
                        (:state = 'WAITING' AND b.status = 'WAITING') OR
                        (:state = 'REJECTED' AND b.status = 'REJECTED')
                    )
                ORDER BY b.start DESC
            """)
    List<Booking> findBookingsByUserAndState(Long userId, boolean isOwner, String state);

    List<Booking> findAllByItemIdIn(Collection<Long> itemIds);

    Booking findByBookerIdAndItemId(Long userId, Long itemId);
}
