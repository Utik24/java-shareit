package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.OwnershipException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repo;
    private final ItemRepository items;
    private final UserRepository users;

    @Override
    public BookingDto create(Long userId, BookingDto dto) {
        if (dto.getStart() == null || dto.getEnd() == null || !dto.getEnd().isAfter(dto.getStart())) {
            throw new ValidationException("Некорректные даты бронирования");
        }
        User booker = users.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = items.findById(dto.getItemId()).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            throw new OwnershipException("Нельзя бронировать свою вещь");
        }
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        var statusesToCheck = List.of(BookingStatus.APPROVED, BookingStatus.WAITING);
        if (repo.existsOverlapping(item.getId(), dto.getStart(), dto.getEnd(), statusesToCheck)) {
            throw new ValidationException("Новые даты пересекаются с существующим бронированием");
        }
        Booking booking = BookingMapper.fromDto(dto, item, booker);
        booking.setStatus(BookingStatus.WAITING);
        booking = repo.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = repo.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (booking.getItem() == null || booking.getItem().getOwner() == null || !booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new OwnershipException("Подтверждать может только владелец");
        }
        if (booking.getStatus() == BookingStatus.APPROVED && approved) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже рассмотрено: " + booking.getStatus());
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        repo.update(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking b = repo.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        Long ownerId = b.getItem() != null && b.getItem().getOwner() != null ? b.getItem().getOwner().getId() : null;
        if (!(b.getBooker() != null && b.getBooker().getId() != null && b.getBooker().getId().equals(userId)) &&
                !(ownerId != null && ownerId.equals(userId))) {
            throw new OwnershipException("Доступ запрещён");
        }
        return BookingMapper.toDto(b);
    }

    @Override
    public List<BookingDto> getByOwner(Long ownerId, BookingState state) {
        users.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> list = switch (state) {
            case ALL      -> repo.findByOwnerId(ownerId);
            case CURRENT  -> repo.findCurrentForOwner(ownerId, now);
            case PAST     -> repo.findPastForOwner(ownerId, now);
            case FUTURE   -> repo.findFutureForOwner(ownerId, now);
            case WAITING  -> repo.findByOwnerAndStatus(ownerId, BookingStatus.WAITING);
            case REJECTED -> repo.findByOwnerAndStatus(ownerId, BookingStatus.REJECTED);
        };
        return list.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> getByBooker(Long userId, BookingState state) {
        users.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> list = switch (state) {
            case ALL      -> repo.findByBookerId(userId);
            case CURRENT  -> repo.findCurrentForBooker(userId, now);
            case PAST     -> repo.findPastForBooker(userId, now);
            case FUTURE   -> repo.findFutureForBooker(userId, now);
            case WAITING  -> repo.findByBookerAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> repo.findByBookerAndStatus(userId, BookingStatus.REJECTED);
        };
        return list.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::toDto)
                .toList();
    }

}