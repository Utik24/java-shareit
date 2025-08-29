package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.OwnershipException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDto.BookingShort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository items;
    private final UserRepository users;
    private final CommentRepository comments;
    private final BookingRepository bookings;

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        User owner = users.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + ownerId + " не найден"));

        if (!org.springframework.util.StringUtils.hasText(dto.getName())) {
            throw new ValidationException("Название вещи не может быть пустым");
        }
        if (!org.springframework.util.StringUtils.hasText(dto.getDescription())) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        if (dto.getAvailable() == null) {
            throw new ValidationException("Поле available обязательно");
        }

        Item item = ItemMapper.fromDto(dto, owner);
        item = items.save(item);
        return ItemMapper.toDto(item);
    }


    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patch) {
        Item item = items.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь " + itemId + " не найдена"));
        if (item.getOwner() == null || !item.getOwner().getId().equals(ownerId)) {
            throw new OwnershipException("Редактировать вещь может только владелец");
        }
        if (StringUtils.hasText(patch.getName())) item.setName(patch.getName());
        if (StringUtils.hasText(patch.getDescription())) item.setDescription(patch.getDescription());
        if (patch.getAvailable() != null) item.setAvailable(patch.getAvailable());
        items.update(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto getById(Long requesterId, Long itemId) {
        Item item = items.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь " + itemId + " не найдена"));
        ItemDto dto = ItemMapper.toDto(item);


        dto.setComments(comments.findByItem_IdOrderByCreatedDesc(itemId).stream()
                .map(c -> new ItemDto.CommentDto(c.getText(), c.getId(),
                        c.getAuthor() != null ? c.getAuthor().getName() : null,
                        c.getCreated() != null ? c.getCreated().toString().substring(0, 19) : null))
                .collect(Collectors.toList()));


        if (item.getOwner() != null && item.getOwner().getId() != null && item.getOwner().getId().equals(requesterId)) {
            LocalDateTime now = LocalDateTime.now();
            var relevant = bookings.findAll().stream()
                    .filter(b -> b.getItem() != null && itemId.equals(b.getItem().getId()))
                    .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                    .collect(Collectors.toList());

            Booking last = relevant.stream()
                    .filter(b -> !b.getStart().isAfter(now))
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            Booking next = relevant.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            dto.setLastBooking(last == null ? null : new BookingShort(last.getId(), last.getBooker() != null ? last.getBooker().getId() : null));
            dto.setNextBooking(next == null ? null : new BookingShort(next.getId(), next.getBooker() != null ? next.getBooker().getId() : null));
        } else {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
        }

        return dto;
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        return items.findByOwnerId(ownerId).stream()
                .map(i -> getById(ownerId, i.getId())) // заполнить last/next/comments
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        return items.searchAvailableByText(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto.CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto) {
        User user = users.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = items.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        boolean can = bookings.existsByBookerFinishedApproved(userId, itemId, LocalDateTime.now());
        if (!can) {
            throw new ValidationException("Нельзя комментировать без завершённого бронирования");
        }

        Comment c = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        c = comments.save(c);

        return ItemDto.CommentDto.builder()
                .id(c.getId())
                .text(c.getText())
                .authorName(c.getAuthor().getName())
                .created(c.getCreated().toString().substring(0, 19))
                .build();
    }

}