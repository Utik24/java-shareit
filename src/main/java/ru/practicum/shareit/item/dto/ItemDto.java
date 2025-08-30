package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    private Long requestId;

    private BookingShort lastBooking;
    private BookingShort nextBooking;

    private java.util.List<CommentDto> comments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingShort {
        private Long id;
        private Long bookerId;
    }
}
