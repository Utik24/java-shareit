package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.RequestDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestDtoTest {

    @Autowired
    private JacksonTester<RequestDto> json;

    @Test
    void testSerialize() throws Exception {
        RequestDto dto = RequestDto.builder()
                .id(1L)
                .description("Нужен холодильник")
                .requestor(100L)
                .created(Timestamp.valueOf(LocalDateTime.of(2025, 1, 1, 12, 0)))
                .build();

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Нужен холодильник");
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(100);
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\n" +
                "  \"id\": 1,\n" +
                "  \"description\": \"Нужен холодильник\",\n" +
                "  \"requestor\": 100,\n" +
                "  \"created\": \"2025-01-01T12:00:00\"\n" +
                "}";


        RequestDto parsed = json.parseObject(content);

        assertThat(parsed.id()).isEqualTo(1L);
        assertThat(parsed.description()).isEqualTo("Нужен холодильник");
        assertThat(parsed.requestor()).isEqualTo(100L);
    }
}
