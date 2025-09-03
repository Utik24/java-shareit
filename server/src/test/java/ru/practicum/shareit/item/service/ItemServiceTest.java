package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.AccessDeniedException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    private Item item;
    private User user;
    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(1L, "Александр", "Dolsa.broadstaff@gmail.com");
        userDto = UserMapper.fromDto(user);

        item = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Норм дрель")
                .available(true)
                .owner(user)
                .build();

        itemDto = ItemDto.builder()
                .id(null)
                .name("Дрель")
                .description("Норм дрель")
                .available(true)
                .owner(null)
                .requestId(null)
                .build();
    }

    @Test
    void addItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.create(1L, itemDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Дрель");
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void getItem() {
        when(itemRepository.findByIdWithCommentsAndAuthors(1L)).thenReturn(Optional.of(item));

        ItemDto result = itemService.getById(1L);

        assertThat(result.getName()).isEqualTo("Дрель");
    }

    @Test
    void getItemNotFound() {
        when(itemRepository.findByIdWithCommentsAndAuthors(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(1L));
    }

    @Test
    void updateItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ItemPatchDto patch = new ItemPatchDto(1L, "Молоток", null, null);

        ItemDto updated = itemService.update(1L, 1L, patch);

        assertThat(updated.getName()).isEqualTo("Молоток");
        assertThat(updated.getDescription()).isEqualTo("Норм дрель");
        assertThat(updated.getAvailable()).isTrue();
        verify(itemRepository).save(any());
    }

    @Test
    void updateItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        ItemPatchDto patch = new ItemPatchDto(1L, "Молоток", null, null);

        assertThrows(NotFoundException.class, () -> itemService.update(1L, 1L, patch));
    }

    @Test
    void updateItemNotOwner() {
        User other = new User(2L, "Bob", "bob@example.com");
        item.setOwner(other);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemPatchDto patch = new ItemPatchDto(1L, "Молоток", null, null);

        assertThrows(NotFoundException.class, () -> itemService.update(1L, 1L, patch));
    }

    @Test
    void deleteItem() {
        itemService.deleteItem(1L);
        verify(itemRepository).deleteById(1L);
    }

    @Test
    void searchItems() {
        when(itemRepository.searchAvailableItems("Дрель")).thenReturn(List.of(item));

        var result = itemService.search("Дрель");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchItemsNull() {
        var r1 = itemService.search(null);
        var r2 = itemService.search("   ");
        assertThat(r1).isEmpty();
        assertThat(r2).isEmpty();
    }

    @Test
    void getItemsByRequestId() {
        when(itemRepository.findItemsByRequestId(1L)).thenReturn(List.of(item));

        var result = itemService.getItemsByRequestId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void createComment() {
        Booking booking = Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .build();

        when(bookingRepository.findByBookerIdAndItemId(1L, 1L)).thenReturn(booking);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Comment comment = Comment.builder().id(1L).comment("Норм").author(user).item(item).build();
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto result = itemService.addComment(1L, 1L, new CommentDto(null, "Норм", null, null));

        assertThat(result.id()).isEqualTo(1L);
        verify(commentRepository).save(any());
    }

    @Test
    void createCommentNotApproved() {
        Booking booking = Booking.builder()
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .build();

        when(bookingRepository.findByBookerIdAndItemId(1L, 1L)).thenReturn(booking);

        assertThrows(AccessDeniedException.class,
                () -> itemService.addComment(1L, 1L, new CommentDto(null, "text", null, null)));
    }

    @Test
    void createCommentNotFinished() {
        Booking booking = Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .build();

        when(bookingRepository.findByBookerIdAndItemId(1L, 1L)).thenReturn(booking);

        assertThrows(AccessDeniedException.class,
                () -> itemService.addComment(1L, 1L, new CommentDto(null, "text", null, null)));
    }

    @Test
    void createCommentItemNotFound() {
        Booking booking = Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .build();

        when(bookingRepository.findByBookerIdAndItemId(1L, 1L)).thenReturn(booking);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addComment(1L, 1L, new CommentDto(null, "text",
                        null, null)));
    }

    @Test
    void getUserItems() {
        Item item2 = Item.builder()
                .id(2L)
                .name("Пила")
                .description("Хорошая")
                .available(true)
                .owner(user)
                .build();

        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item, item2));

        LocalDateTime now = LocalDateTime.now();

        Booking past = Booking.builder()
                .id(10L)
                .item(item)
                .start(now.minusDays(5))
                .end(now.minusDays(2))
                .status(BookingStatus.APPROVED)
                .build();

        Booking future = Booking.builder()
                .id(11L)
                .item(item)
                .start(now.plusDays(2))
                .end(now.plusDays(3))
                .status(BookingStatus.APPROVED)
                .build();

        Booking otherFuture = Booking.builder()
                .id(12L)
                .item(item2)
                .start(now.plusDays(5))
                .end(now.plusDays(6))
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByItemIdIn(List.of(1L, 2L))).thenReturn(List.of(past, future, otherFuture));

        var result = itemService.getByOwner(1L);

        Optional<ItemDto> dto1 = result.stream().filter(d -> d.getId() == 1L).findFirst();
        Optional<ItemDto> dto2 = result.stream().filter(d -> d.getId() == 2L).findFirst();

        assertThat(dto1).isPresent();
        assertThat(dto1.get().getLastBooking()).isNotNull();
        assertThat(dto1.get().getNextBooking()).isNotNull();
        assertThat(dto1.get().getLastBooking()).isEqualTo(past.getEnd());
        assertThat(dto1.get().getNextBooking()).isEqualTo(future.getStart());

        assertThat(dto2).isPresent();
        assertThat(dto2.get().getLastBooking()).isNull();
        assertThat(dto2.get().getNextBooking()).isEqualTo(otherFuture.getStart());
    }
}
