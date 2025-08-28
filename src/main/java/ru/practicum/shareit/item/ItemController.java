package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HasUserHeader;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static ru.practicum.shareit.HasUserHeader.USER_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController  {

    private final ItemService service;

    // Добавление новой вещи
    @PostMapping
    public ItemDto create(@RequestHeader(USER_HEADER) Long userId,
                          @Valid @RequestBody ItemDto dto) {
        return service.create(userId, dto);
    }

    // Редактирование вещи (частичное)
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto patch) {
        return service.update(userId, itemId, patch);
    }

    // Просмотр конкретной вещи
    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(USER_HEADER) Long userId,
                           @PathVariable Long itemId) {
        return service.getById(userId, itemId);
    }

    // Список вещей владельца
    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader(USER_HEADER) Long userId) {
        return service.getByOwner(userId);
    }

    // Поиск по тексту (только доступные)
    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        return service.search(text);
    }
}
