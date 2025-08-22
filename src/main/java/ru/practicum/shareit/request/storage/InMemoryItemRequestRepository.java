package ru.practicum.shareit.request.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRequestRepository implements ItemRequestRepository {

    private final Map<Long, ItemRequest> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public ItemRequest save(ItemRequest request) {
        long id = seq.incrementAndGet();
        request.setId(id);
        storage.put(id, request);
        return request;
    }

    @Override
    public Optional<ItemRequest> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<ItemRequest> findByRequestorId(Long requestorId) {
        return storage.values().stream()
                .filter(r -> r.getRequestor() != null && Objects.equals(r.getRequestor().getId(), requestorId))
                .sorted(Comparator.comparing(ItemRequest::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequest> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated))
                .collect(Collectors.toList());
    }
}
