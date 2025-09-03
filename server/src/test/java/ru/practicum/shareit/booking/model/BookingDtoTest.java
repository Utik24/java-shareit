package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 1, 1, 12, 0))
                .end(LocalDateTime.of(2025, 1, 2, 12, 0))
                .item(ItemDto.builder().id(10L).name("Item").description("Desc").available(true).build())
                .booker(UserDto.builder().id(100L).name("User").email("dolsa.broadstaff@gmail.com").build())
                .status(BookingStatus.APPROVED)
                .build();

        var result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Item");
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("User");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\n" +
                "  \"id\": 1,\n" +
                "  \"start\": \"2025-01-01T12:00:00\",\n" +
                "  \"end\": \"2025-01-02T12:00:00\",\n" +
                "  \"item\": {\"id\": 10, \"name\": \"Item\", \"description\": \"Desc\", \"available\": true},\n" +
                "  \"booker\": {\"id\": 100, \"name\": \"User\", \"email\": \"dolsa.broadstaff@gmail.com\"},\n" +
                "  \"status\": \"APPROVED\"\n" +
                "}";


        BookingDto parsed = json.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(1L);
        assertThat(parsed.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(parsed.getItem().getName()).isEqualTo("Item");
        assertThat(parsed.getBooker().getEmail()).isEqualTo("dolsa.broadstaff@gmail.com");
    }
}
