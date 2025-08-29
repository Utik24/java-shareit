package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static ru.practicum.shareit.HasUserHeader.USER_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_HEADER) Long ownerId,
                          @RequestBody ItemDto dto) {
        return service.create(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto patch) {
        return service.update(ownerId, itemId, patch);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(USER_HEADER) Long requesterId,
                           @PathVariable Long itemId) {
        return service.getById(requesterId, itemId);
    }

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader(USER_HEADER) Long ownerId) {
        return service.getByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        return service.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentCreateDto dto) {
        return service.addComment(userId, itemId, dto);
    }
}
