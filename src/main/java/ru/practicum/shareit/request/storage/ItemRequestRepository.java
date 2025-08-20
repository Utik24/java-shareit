package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository {
    ItemRequest save(ItemRequest request);

    Optional<ItemRequest> findById(Long id);

    List<ItemRequest> findByRequestorId(Long requestorId);

    List<ItemRequest> findAll();
}
