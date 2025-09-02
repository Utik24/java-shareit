package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItems;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User testUser;
    private UserDto testUserDto;
    private Request testRequest;
    private RequestDto testRequestDto;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User(1L, "Aleksandr", "Dolsa.broadstaff@gmail.com");
        testUserDto = UserMapper.fromDto(testUser);
        testRequestDto = new RequestDto(null, "Описание", testUser.getId(), Timestamp.from(Instant.MIN));
        testRequest = new Request(1L, "Описание", testUser, null);
    }

    @Test
    void createRequest() {
        when(userService.getById(testUser.getId())).thenReturn(testUserDto);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        RequestDto result = requestService.createRequest(testUser.getId(), testRequestDto);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.description()).isEqualTo("Описание");
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void getRequests() {
        when(requestRepository.findByRequestorIdOrderByCreatedDesc(testUser.getId()))
                .thenReturn(List.of(testRequest));
        when(itemService.getItemsByRequestId(testRequest.getId())).thenReturn(List.of());

        List<RequestWithItems> requests = requestService.getRequests(testUser.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).id()).isEqualTo(1L);
        assertThat(requests.get(0).items()).isEmpty();
    }


    @Test
    void getAllRequests() {
        when(requestRepository.findAllByOrderByCreatedDesc())
                .thenReturn(List.of(testRequest));

        List<RequestDto> requests = requestService.getAllRequests();

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).id()).isEqualTo(1L);
    }


    @Test
    void getRequestById() {
        when(requestRepository.findById(testRequest.getId())).thenReturn(Optional.of(testRequest));
        when(itemService.getItemsByRequestId(testRequest.getId())).thenReturn(List.of());

        RequestWithItems result = requestService.getRequestById(testRequest.getId());

        assertThat(result.id()).isEqualTo(testRequest.getId());
        assertThat(result.items()).isEmpty();
    }

    @Test
    void getRequestByIdNotFound() {
        when(requestRepository.findById(testRequest.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> requestService.getRequestById(testRequest.getId()));
    }
}
