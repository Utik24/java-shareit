package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("Александр")
                .email("Dolsa.broadstaff@gmail.com")
                .build();

        var result = json.write(user);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Александр");
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("Dolsa.broadstaff@gmail.com");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Александр\",\n" +
                "  \"email\": \"Dolsa.broadstaff@gmail.com\"\n" +
                "}";


        UserDto parsed = json.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(1L);
        assertThat(parsed.getName()).isEqualTo("Александр");
        assertThat(parsed.getEmail()).isEqualTo("Dolsa.broadstaff@gmail.com");
    }
}
