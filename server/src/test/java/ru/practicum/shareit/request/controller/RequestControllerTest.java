package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItems;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestService requestService;

    @Test
    void createRequest() throws Exception {
        RequestDto requestDto = RequestDto.builder()
                .id(1L)
                .description("Холодильник")
                .requestor(null)
                .created(null)
                .build();

        Mockito.when(requestService.createRequest(eq(1L), any(RequestDto.class))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Холодильник"));
    }

    @Test
    void getRequests() throws Exception {
        RequestWithItems requestWithItems = new RequestWithItems(1L, "Холодильник", null, List.of());
        Mockito.when(requestService.getRequests(1L)).thenReturn(List.of(requestWithItems));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Холодильник"));
    }

    @Test
    void getAllRequests() throws Exception {
        RequestDto requestDto = RequestDto.builder()
                .id(1L)
                .description("Холодильник")
                .requestor(null)
                .created(null)
                .build();

        Mockito.when(requestService.getAllRequests()).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Холодильник"));
    }

    @Test
    void getRequest() throws Exception {
        RequestWithItems requestWithItems = new RequestWithItems(1L, "Холодильник", null, List.of());
        Mockito.when(requestService.getRequestById(1L)).thenReturn(requestWithItems);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Холодильник"));
    }
}
