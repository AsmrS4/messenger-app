package com.example.authorization_service.api;

import com.example.authorization_service.api.models.AuthCredentials;
import com.example.authorization_service.api.models.AuthResponse;
import com.example.authorization_service.api.models.UserCreatePayload;
import com.example.authorization_service.core.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void logoutUser() {
        authService.invalidateSession();
    }

    @PostMapping("/session")
    public void validateUserSession() {
        authService.checkSession();
    }
}
