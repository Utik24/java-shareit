package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.NotFoundException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private ErrorHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ErrorHandler();
    }

    @Test
    void handleValidationException_shouldReturnBadRequest() {
        FieldError fieldError = new FieldError("objectName", "fieldName", "не может быть пустым");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorHandler.ErrorResponse> response =
                exceptionHandler.handleValidationException(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().message().contains("Поле 'fieldName': не может быть пустым"));
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
