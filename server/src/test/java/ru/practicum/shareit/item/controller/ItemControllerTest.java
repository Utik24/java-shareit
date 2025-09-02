package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void addItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Ножницы")
                .description("Работать")
                .available(true)
                .build();

        Mockito.when(itemService.create(eq(1L), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ножницы"))
                .andExpect(jsonPath("$.description").value("Работать"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void updateItem() throws Exception {
        ItemPatchDto patchDto = new ItemPatchDto(null, "Ножницы", null, true);

        ItemDto updatedItem = ItemDto.builder()
                .id(1L)
                .name("Ножницы+")
                .description("Работать")
                .available(true)
                .build();

        Mockito.when(itemService.update(eq(1L), eq(1L), any(ItemPatchDto.class)))
                .thenReturn(updatedItem);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ножницы+"));
    }

    @Test
    void getItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Ножницы")
                .description("Работать")
                .available(true)
                .build();

        Mockito.when(itemService.getById(1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ножницы"));
    }

    @Test
    void getUserItems() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Ножницы")
                .description("Работать")
                .available(true)
                .build();

        Mockito.when(itemService.getByOwner(1L)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void searchItems() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Ножницы")
                .description("Работать")
                .available(true)
                .build();

        Mockito.when(itemService.search("Ножницы")).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Ножницы"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void deleteItem() throws Exception {
        Mockito.doNothing().when(itemService).deleteItem(1L);

        mockMvc.perform(delete("/items")
                        .param("id", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Супер ножницы")
                .authorName("Александр")
                .build();

        Mockito.when(itemService.addComment(eq(1L), eq(1L), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Супер ножницы"))
                .andExpect(jsonPath("$.authorName").value("Александр"));
    }
}
