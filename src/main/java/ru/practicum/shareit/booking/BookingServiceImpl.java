package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.OwnershipException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repo;
    private final UserRepository users;
    private final ItemRepository items;

    @Override
    public BookingDto create(Long userId, BookingDto dto) {
        User booker = users.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
        Item item = items.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + dto.getItemId()));
        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new IllegalStateException("Вещь недоступна для бронирования");
        }
        Booking booking = BookingMapper.fromDto(dto, item, booker);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toDto(repo.save(booking));
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = repo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: " + bookingId));
        if (booking.getItem().getOwner() == null ||
                !booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new OwnershipException("Только владелец может подтверждать бронирование");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        repo.update(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = repo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: " + bookingId));
        if (!(booking.getBooker().getId().equals(userId) ||
                booking.getItem().getOwner().getId().equals(userId))) {
            throw new OwnershipException("Нет доступа к бронированию");
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getByBooker(Long userId) {
        return repo.findByBookerId(userId).stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByOwner(Long ownerId) {
        return repo.findByOwnerId(ownerId).stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
