package com.example.authorization_service.core.http.client;

import com.example.authorization_service.api.models.UserCreatePayload;
import com.example.authorization_service.configuration.UserClientConfiguration;
import com.example.authorization_service.core.http.models.UserProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
        name = "user-service",
        url = "${user-service.url}",
        configuration = UserClientConfiguration.class
)
public interface UserClient {
    @PostMapping("/api/users/new")
    UserProfile createNewUser(@RequestBody UserCreatePayload payload);

    @GetMapping("/api/users/profile/{userId}")
    void existsByUUID(@PathVariable("userId")UUID userId);
}
