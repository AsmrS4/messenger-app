package com.example.user_service.core.http;

import com.example.user_service.api.models.UpdateCredentials;
import com.example.user_service.configuration.AuthClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "authentication-service",
        url = "${auth-service.url}",
        configuration = AuthClientConfiguration.class
)
public interface AuthenticationClient {
    @PostMapping("/api/auth/session")
    void checkSession(@RequestHeader("Authorization") String authHeader);

    @PostMapping("/api/auth/credentials")
    void updateCredentials(@RequestBody UpdateCredentials credentials);
}
