package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaItemRepository implements ItemRepository {
    private final SpringDataItemJpa jpa;

    @Override
    public Item save(Item item) {
        return jpa.save(item);
    }

    @Override
    public Item update(Item item) {
        return jpa.save(item);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Item> findAll() {
        return jpa.findAll();
    }

    @Override
    public List<Item> findByOwnerId(Long ownerId) {
        return jpa.findByOwner_Id(ownerId);
    }

    @Override
    public List<Item> searchAvailableByText(String text) {
        return jpa.searchAvailableByText(text);
    }
}
