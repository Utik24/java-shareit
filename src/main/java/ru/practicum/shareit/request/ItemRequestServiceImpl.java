package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repo;
    private final UserRepository users;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        User user = users.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
        ItemRequest request = ItemRequestMapper.fromDto(dto, user);
        return ItemRequestMapper.toDto(repo.save(request));
    }

    @Override
    public List<ItemRequestDto> getByRequestor(Long userId) {
        return repo.findByRequestorId(userId).stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll() {
        return repo.findAll().stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        users.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
        return repo.findById(requestId)
                .map(ItemRequestMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Запрос не найден: " + requestId));
    }
}
