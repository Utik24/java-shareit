package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.AccessDeniedException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info(itemDto.toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));

        Item item = ItemMapper.fromDto(itemDto);
        item.setOwner(user);

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemPatchDto itemPatchDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Предмет для обновления не найден."));

        if (!Objects.equals(userId, item.getOwner().getId())) {
            throw new NotFoundException("Не найден предмет для редактирования.");
        }

        item = item.toBuilder().name(itemPatchDto.name() != null ? itemPatchDto.name() : item.getName())
                .description(itemPatchDto.description() != null ? itemPatchDto.description() : item.getDescription())
                .available(itemPatchDto.available() != null ? itemPatchDto.available() : item.getAvailable())
                .build();

        itemRepository.save(item);

        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto getById(Long id) {
        Item item = itemRepository.findByIdWithCommentsAndAuthors(id)
                .orElseThrow(() -> new NotFoundException("Предмет не найден."));
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);

        List<Booking> bookings = bookingRepository.findAllByItemIdIn(
                items.stream().map(Item::getId).toList()
        );

        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), List.of());

                    return ItemMapper.toDto(item).toBuilder()
                            .lastBooking(itemBookings.stream()
                                    .filter(b -> b.getEnd().isBefore(now))
                                    .max(Comparator.comparing(Booking::getEnd))
                                    .map(Booking::getEnd)
                                    .orElse(null))
                            .nextBooking(itemBookings.stream()
                                    .filter(b -> b.getStart().isAfter(now))
                                    .min(Comparator.comparing(Booking::getStart))
                                    .map(Booking::getStart)
                                    .orElse(null))
                            .build();
                })
                .toList();
    }

    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> search(String query) {

        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        return itemRepository.searchAvailableItems(query).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {

        Booking booking = bookingRepository.findByBookerIdAndItemId(userId, itemId);

        if (!booking.getStatus().equals(BookingStatus.APPROVED) || LocalDateTime.now().isBefore(booking.getEnd())) {
            throw new AccessDeniedException("Вы не можете оставить комментарий");
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        var item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет отсутствует"));

        Comment comment = Comment.builder()
                .author(user)
                .item(item)
                .comment(commentDto.text())
                .created(LocalDateTime.now())
                .build();

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<Item> getItemsByRequestId(Long requestId) {
        return itemRepository.findItemsByRequestId(requestId).stream()
                .toList();
    }

    @Override
    public List<Item> getItemsByRequestIds(List<Long> requestIds) {
        return itemRepository.findItemsByRequestIds(requestIds);
    }
}