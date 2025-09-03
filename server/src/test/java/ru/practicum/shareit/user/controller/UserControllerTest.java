package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void addUser_shouldReturnCreatedUserWithNameAleksandr() throws Exception {
        UserDto userDto = UserMapper.fromDto(
                new User(1L, "Александр", "Dolsa.broadstaff@gmail.com"));
        Mockito.when(userService.create(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Александр"))
                .andExpect(jsonPath("$.email").value("Dolsa.broadstaff@gmail.com"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUserWithNameAleksandr() throws Exception {
        UserPatchDto patchDto = new UserPatchDto(null, "Александр", null);
        UserDto updatedUser = UserMapper.fromDto(
                new User(1L, "Александр", "Dolsa.broadstaff@gmail.com"));

        Mockito.when(userService.update(eq(1L), any(UserPatchDto.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Александр"))
                .andExpect(jsonPath("$.email").value("Dolsa.broadstaff@gmail.com"));
    }

    @Test
    void getUser_shouldReturnUserAleksandr() throws Exception {
        UserDto userDto = UserMapper.fromDto(
                new User(1L, "Александр", "Dolsa.broadstaff@gmail.com"));
        Mockito.when(userService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Александр"))
                .andExpect(jsonPath("$.email").value("Dolsa.broadstaff@gmail.com"));
    }

    @Test
    void deleteUser_shouldReturnNoContentForAleksandr() throws Exception {
        Mockito.doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}
