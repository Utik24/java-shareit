package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.HasUserHeader;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController implements HasUserHeader {


    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_HEADER) Long userId,
                                 @Valid @RequestBody ItemRequestDto dto) {
        return service.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> getByRequestor(@RequestHeader(USER_HEADER) Long userId) {
        return service.getByRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(USER_HEADER) Long userId) {
        return service.getAll();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(USER_HEADER) Long userId,
                                  @PathVariable Long requestId) {
        return service.getById(userId, requestId);
    }
}
