package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItems;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static ru.practicum.shareit.UserHeader.USER_HEADER;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RequestDto createRequest(@RequestHeader(USER_HEADER) Long userId,
                             @RequestBody RequestDto requestDto) {
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping
    List<RequestWithItems> getRequests(@RequestHeader(USER_HEADER) Long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    List<RequestDto> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("{requestId}")
    RequestWithItems getRequest(@PathVariable("requestId") Long requestId) {
        return requestService.getRequestById(requestId);
    }
}
