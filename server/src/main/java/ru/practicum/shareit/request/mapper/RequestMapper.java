package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItems;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestMapper {


    public static Request mapToItemRequest(final RequestDto requestDto) {
        User requestor = requestDto.requestor() != null ? User.builder().id(requestDto.requestor()).build()
                : null;

        return Request.builder()
                .description(requestDto.description())
                .requestor(requestor)
                .created(requestDto.created() == null ? Timestamp.from(Instant.now()) : requestDto.created())
                .build();
    }

    public static RequestDto mapToItemRequestDto(final Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(request.getRequestor().getId() == null ? null : request.getRequestor().getId())
                .created(request.getCreated())
                .build();
    }

    public static RequestWithItems mapToItemRequestWithItems(Request request, List<ItemRequestDto> items) {
        return RequestWithItems.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }
}
