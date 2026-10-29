package com.example.user_service.core.http;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "authentication-service",
        url = "auth-service.url",
        configuration = AuthenticationClient.class
)
public interface AuthenticationClient {
    @PostMapping("/api/auth/session")
    void checkSession(@RequestHeader("Authorization") String authHeader);
}
