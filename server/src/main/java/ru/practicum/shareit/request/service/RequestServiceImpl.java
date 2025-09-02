package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItems;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public RequestDto createRequest(Long userId, RequestDto requestDto) {
        Request request = RequestMapper.mapToItemRequest(requestDto);
        request.setRequestor(UserMapper.fromDto(userService.getById(userId)));

        return RequestMapper.mapToItemRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestWithItems> getRequests(Long userId) {
        return requestRepository.findByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(request -> {
                    List<ItemRequestDto> items = itemService.getItemsByRequestId(request.getId()).stream()
                            .map(ItemMapper::mapToItemRequestDto)
                            .toList();
                    return RequestMapper.mapToItemRequestWithItems(request, items);
                })
                .toList();
    }

    @Override
    public List<RequestDto> getAllRequests() {
        return requestRepository.findAllByOrderByCreatedDesc().stream()
                .map(RequestMapper::mapToItemRequestDto)
                .toList();
    }


    @Override
    public RequestWithItems getRequestById(Long requestId) {
        var items = itemService.getItemsByRequestId(requestId).stream()
                .map(ItemMapper::mapToItemRequestDto)
                .toList();
        return RequestMapper.mapToItemRequestWithItems(requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос отсутствует")), items);
    }
}
