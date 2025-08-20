package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("timestamp", Instant.now().toString(),
                        "status", 404,
                        "error", "Not Found",
                        "message", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("timestamp", Instant.now().toString(),
                        "status", 409,
                        "error", "Bad Request",
                        "message", ex.getMessage()));
    }

    @ExceptionHandler(OwnershipException.class)
    public ResponseEntity<Map<String, Object>> handleOwnership(OwnershipException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("timestamp", Instant.now().toString(),
                        "status", 403,
                        "error", "Forbidden",
                        "message", ex.getMessage()));
    }

}
