package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto dto);

    List<ItemRequestDto> getByRequestor(Long userId);

    List<ItemRequestDto> getAll();

    ItemRequestDto getById(Long userId, Long requestId);
}
