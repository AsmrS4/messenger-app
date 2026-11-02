package com.example.user_service.api.controller;

import com.example.user_service.api.models.ErrorResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.authentication.AuthenticationServiceException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {
        return new ResponseEntity<>(createResponseBody(ex), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(createResponseBody(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        return new ResponseEntity<>(createResponseBody(ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationServiceException(AuthenticationServiceException ex) {
        return new ResponseEntity<>(createResponseBody(ex), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(createResponseBody(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse createResponseBody() {
        LocalDateTime timestamp = LocalDateTime.now();
        return new ErrorResponse("Ошибка сервера", timestamp);
    }

    private ErrorResponse createResponseBody(Exception ex) {
        LocalDateTime timestamp = LocalDateTime.now();
        return new ErrorResponse(ex.getMessage(), timestamp);
    }
}
