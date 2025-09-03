package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private ErrorHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ErrorHandler();
    }

    @Test
    void handleNotFoundException_shouldReturnNotFound() {
        NotFoundException ex = new NotFoundException("Не найдено");

        ResponseEntity<ErrorHandler.ErrorResponse> response =
                exceptionHandler.handleNotFoundException(ex);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Не найдено", response.getBody().message());
    }

    @Test
    void handleUnhandledExceptions_shouldReturnInternalServerError() {
        Exception ex = new Exception("Что-то пошло не так");

        ResponseEntity<ErrorHandler.ErrorResponse> response =
                exceptionHandler.handleUnhandledExceptions(ex);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Произошла внутренняя ошибка сервера", response.getBody().message());
    }
}
