package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ru.practicum.shareit.ShareItApp.class)
@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Test
    void addUser() {
        User user = new User(null, "Aleksandr", "Dolsa.Broadstaff@gmail.com");
        UserDto userDto = UserMapper.fromDto(user);

        UserDto savedUser = userService.create(userDto);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Aleksandr");
        assertThat(savedUser.getEmail()).isEqualTo("Dolsa.Broadstaff@gmail.com");

        UserDto fetchedUser = userService.getById(savedUser.getId());
        assertThat(fetchedUser.getName()).isEqualTo("Aleksandr");
        assertThat(fetchedUser.getEmail()).isEqualTo("Dolsa.Broadstaff@gmail.com");
    }
}
