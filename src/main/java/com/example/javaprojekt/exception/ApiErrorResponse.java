package com.example.javaprojekt.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error; // np. "Bad Request"
    private String message; // Ogolny komunikat
    private String path;
    private Map<String, String> validationErrors; // Dla bledow walidacji pol

    public ApiErrorResponse(HttpStatus httpStatus, String message, String path, Map<String, String> validationErrors) {
        this.timestamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
    }

    public ApiErrorResponse(HttpStatus httpStatus, String message, String path) {
        this(httpStatus, message, path, null);
    }
}