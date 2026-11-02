package com.example.authorization_service.api.controller;

import com.example.authorization_service.api.models.AuthCredentials;
import com.example.authorization_service.api.models.AuthResponse;
import com.example.authorization_service.api.models.UpdateCredentials;
import com.example.authorization_service.api.models.UserCreatePayload;
import com.example.authorization_service.core.service.auth.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@Slf4j
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

    @PostMapping("/update")
    @SecurityRequirement(name = "bearerAuth")
    public void updateCredentials(@RequestBody UpdateCredentials credentials) {
        authService.updateCredentials(credentials);
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public void logoutUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Отсутствует заголовок авторизации");
        }
        String token = authHeader.substring(7);
        authService.invalidateSession(token);
    }

    @PostMapping("/session")
    @SecurityRequirement(name = "bearerAuth")
    public void validateUserSession(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        log.error(authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Отсутствует заголовок авторизации");
        }
        String token = authHeader.substring(7);
        authService.checkSession(token);
    }
}
