package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItems;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(Long userId, RequestDto requestDto);

    List<RequestWithItems> getRequests(Long userId);

    List<RequestDto> getAllRequests();

    RequestWithItems getRequestById(Long requestId);
}
