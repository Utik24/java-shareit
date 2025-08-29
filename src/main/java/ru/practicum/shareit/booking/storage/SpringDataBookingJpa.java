package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataBookingJpa extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

    @Query("""
            select b from Booking b
            where b.booker.id = :uid and b.start <= :now and b.end >= :now
            order by b.start desc
            """)
    List<Booking> findCurrentForBooker(@Param("uid") Long userId, @Param("now") LocalDateTime now);

    @Query("""
            select b from Booking b
            where b.booker.id = :uid and b.end < :now
            order by b.start desc
            """)
    List<Booking> findPastForBooker(@Param("uid") Long userId, @Param("now") LocalDateTime now);

    @Query("""
            select b from Booking b
            where b.booker.id = :uid and b.start > :now
            order by b.start desc
            """)
    List<Booking> findFutureForBooker(@Param("uid") Long userId, @Param("now") LocalDateTime now);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    @Query("""
            select b from Booking b
            where b.item.owner.id = :oid and b.start <= :now and b.end >= :now
            order by b.start desc
            """)
    List<Booking> findCurrentForOwner(@Param("oid") Long ownerId, @Param("now") LocalDateTime now);

    @Query("""
            select b from Booking b
            where b.item.owner.id = :oid and b.end < :now
            order by b.start desc
            """)
    List<Booking> findPastForOwner(@Param("oid") Long ownerId, @Param("now") LocalDateTime now);

    @Query("""
            select b from Booking b
            where b.item.owner.id = :oid and b.start > :now
            order by b.start desc
            """)
    List<Booking> findFutureForOwner(@Param("oid") Long ownerId, @Param("now") LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    @Query("""
            select (count(b) > 0) from Booking b
            where b.booker.id = :uid and b.item.id = :itemId
              and b.status = ru.practicum.shareit.booking.BookingStatus.APPROVED
              and b.end < :now
            """)
    boolean existsFinishedApproved(@Param("uid") Long userId, @Param("itemId") Long itemId, @Param("now") LocalDateTime now);
}