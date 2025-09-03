package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItems;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequestMapperTest {

    @Test
    void mapToItemRequest_generatesTimestampAndUser() {
        RequestDto dto = RequestDto.builder()
                .description("desc")
                .requestor(1L)
                .build();

        Request request = RequestMapper.mapToItemRequest(dto);

        assertEquals("desc", request.getDescription());
        assertEquals(1L, request.getRequestor().getId());
        assertNotNull(request.getCreated());
    }

    @Test
    void mapToItemRequestDto_mapsFields() {
        User requestor = User.builder().id(1L).build();
        Timestamp created = Timestamp.from(Instant.EPOCH);
        Request request = Request.builder()
                .id(5L)
                .description("desc")
                .requestor(requestor)
                .created(created)
                .build();

        RequestDto dto = RequestMapper.mapToItemRequestDto(request);

        assertEquals(5L, dto.id());
        assertEquals("desc", dto.description());
        assertEquals(1L, dto.requestor());
        assertEquals(created, dto.created());
    }

    @Test
    void mapToItemRequestWithItems_includesItems() {
        Request request = Request.builder()
                .id(5L)
                .description("desc")
                .created(Timestamp.from(Instant.EPOCH))
                .build();

        ItemRequestDto itemDto = ItemRequestDto.builder()
                .itemId(10L)
                .name("item")
                .ownerId(2L)
                .build();

        RequestWithItems withItems = RequestMapper.mapToItemRequestWithItems(request, List.of(itemDto));

        assertEquals(5L, withItems.id());
        assertEquals("desc", withItems.description());
        assertEquals(List.of(itemDto), withItems.items());
    }
}