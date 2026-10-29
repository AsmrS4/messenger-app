package com.example.authorization_service.api.controller;

import com.example.authorization_service.api.models.AuthCredentials;
import com.example.authorization_service.api.models.AuthResponse;
import com.example.authorization_service.api.models.UserCreatePayload;
import com.example.authorization_service.core.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthorizationController {
    private final AuthService authService;

    public AuthorizationController(@Autowired AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse loginUser(@RequestBody AuthCredentials credentials) {
        return authService.loginUser(credentials);
    }

    @PostMapping("/register")
    public AuthResponse registerUser(@RequestBody UserCreatePayload payload){
        return authService.registerUser(payload);
    }

    @PostMapping("/logout")
    public void logoutUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Отсутствует заголовок авторизации");
        }
        String token = authHeader.substring(7);
        authService.invalidateSession(token);
    }

    @PostMapping("/session")
    public void validateUserSession(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Отсутствует заголовок авторизации");
        }
        String token = authHeader.substring(7);
        authService.checkSession(token);
    }
}
