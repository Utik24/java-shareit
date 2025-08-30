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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
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

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(Long ownerId, ItemCreateDto dto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + ownerId + " не найден"));
        Item item = ItemMapper.fromCreateDto(dto, owner);
        item = itemRepository.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patch) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь " + itemId + " не найдена"));
        if (item.getOwner() == null || !item.getOwner().getId().equals(ownerId)) {
            throw new OwnershipException("Редактировать вещь может только владелец");
        }
        if (StringUtils.hasText(patch.getName())) item.setName(patch.getName());
        if (StringUtils.hasText(patch.getDescription())) item.setDescription(patch.getDescription());
        if (patch.getAvailable() != null) item.setAvailable(patch.getAvailable());
        itemRepository.update(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto getById(Long requesterId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь " + itemId + " не найдена"));

        ItemDto dto = ItemMapper.toDto(item);

        dto.setComments(
                commentRepository.findByItem_IdOrderByCreatedDesc(itemId).stream()
                        .map(ItemMapper::toCommentDto)
                        .collect(Collectors.toList())
        );

        if (item.getOwner() != null && item.getOwner().getId() != null
                && item.getOwner().getId().equals(requesterId)) {

            LocalDateTime now = LocalDateTime.now();
            var relevant = bookingRepository.findAll().stream()
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

            dto.setLastBooking(last == null ? null :
                    new BookingShort(last.getId(), last.getBooker() != null ? last.getBooker().getId() : null));
            dto.setNextBooking(next == null ? null :
                    new BookingShort(next.getId(), next.getBooker() != null ? next.getBooker().getId() : null));
        } else {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
        }

        return dto;
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        var ownedItems = itemRepository.findByOwnerId(ownerId);
        if (ownedItems.isEmpty()) return List.of();

        var itemIds = ownedItems.stream().map(Item::getId).toList();

        var commentsByItemId = commentRepository.findByItem_IdInOrderByCreatedDesc(itemIds).stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        var approvedByItemId = bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.APPROVED).stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        var now = LocalDateTime.now();

        return ownedItems.stream()
                .map(item -> {
                    var dto = ItemMapper.toDto(item);

                    var cmts = commentsByItemId.getOrDefault(item.getId(), List.of());
                    dto.setComments(cmts.stream().map(ItemMapper::toCommentDto).toList());

                    var bookList = approvedByItemId.getOrDefault(item.getId(), List.of());
                    Booking last = bookList.stream()
                            .filter(b -> !b.getStart().isAfter(now))
                            .max(Comparator.comparing(Booking::getStart))
                            .orElse(null);
                    Booking next = bookList.stream()
                            .filter(b -> b.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    dto.setLastBooking(last == null ? null :
                            new ItemDto.BookingShort(last.getId(),
                                    last.getBooker() != null ? last.getBooker().getId() : null));
                    dto.setNextBooking(next == null ? null :
                            new ItemDto.BookingShort(next.getId(),
                                    next.getBooker() != null ? next.getBooker().getId() : null));

                    return dto;
                })
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        return itemRepository.searchAvailableByText(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        boolean can = bookingRepository.existsByBookerFinishedApproved(userId, itemId, LocalDateTime.now());
        if (!can) {
            throw new ValidationException("Нельзя комментировать без завершённого бронирования");
        }

        Comment c = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        c = commentRepository.save(c);

        return ItemMapper.toCommentDto(c);
    }
}