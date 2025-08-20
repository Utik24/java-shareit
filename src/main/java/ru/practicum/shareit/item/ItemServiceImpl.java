package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.OwnershipException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository items;
    private final UserRepository users;

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        User owner = users.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Владелец " + ownerId + " не найден"));
        Item item = ItemMapper.fromDto(dto);
        item.setOwner(owner);
        return ItemMapper.toDto(items.save(item));
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patch) {
        Item item = items.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь " + itemId + " не найдена"));
        if (item.getOwner() == null || !item.getOwner().getId().equals(ownerId)) {
            throw new OwnershipException("Редактировать вещь может только владелец");
        }
        if (StringUtils.hasText(patch.getName())) item.setName(patch.getName());
        if (StringUtils.hasText(patch.getDescription())) item.setDescription(patch.getDescription());
        if (patch.getAvailable() != null) item.setAvailable(patch.getAvailable());
        items.update(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto getById(Long requesterId, Long itemId) {
        // В спринте 1 логика одна: вернём DTO, requesterId нужен для будущего расширения
        return items.findById(itemId)
                .map(ItemMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Вещь " + itemId + " не найдена"));
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        return items.findByOwnerId(ownerId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of(); // По ТЗ при пустом тексте — пустой список
        }
        return items.searchAvailableByText(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
