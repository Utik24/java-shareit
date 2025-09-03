package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("отзыв")
                .authorName("Александр")
                .created(LocalDateTime.of(2025, 1, 1, 12, 0))
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(10L)
                .name("Дрель")
                .description("Супер дрель")
                .available(true)
                .owner(100L)
                .requestId(200L)
                .lastBooking(LocalDateTime.of(2025, 1, 1, 10, 0))
                .nextBooking(LocalDateTime.of(2025, 1, 5, 10, 0))
                .comments(List.of(comment))
                .build();

        var result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(100);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(200);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo("отзыв");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo("Александр");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\n" +
                "  \"id\": 10,\n" +
                "  \"name\": \"Дрель\",\n" +
                "  \"description\": \"Супер дрель\",\n" +
                "  \"available\": true,\n" +
                "  \"owner\": 100,\n" +
                "  \"requestId\": 200,\n" +
                "  \"lastBooking\": \"2025-01-01T10:00:00\",\n" +
                "  \"nextBooking\": \"2025-01-05T10:00:00\",\n" +
                "  \"comments\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"text\": \"отзыв\",\n" +
                "      \"authorName\": \"Александр\",\n" +
                "      \"created\": \"2025-01-01T12:00:00\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";


        ItemDto parsed = json.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(10L);
        assertThat(parsed.getName()).isEqualTo("Дрель");
        assertThat(parsed.getDescription()).isEqualTo("Супер дрель");
        assertThat(parsed.getOwner()).isEqualTo(100L);
        assertThat(parsed.getRequestId()).isEqualTo(200L);
        assertThat(parsed.getComments()).hasSize(1);
        assertThat(parsed.getComments().get(0).text()).isEqualTo("отзыв");
        assertThat(parsed.getComments().get(0).authorName()).isEqualTo("Александр");
    }
}
