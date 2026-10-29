package com.example.authorization_service.core.resolver;

import com.example.authorization_service.api.models.UserCreatePayload;
import com.example.authorization_service.core.http.client.UserClient;
import com.example.authorization_service.core.http.models.UserProfile;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/*
*
* Валидация параметров лежит на User-service
* */

@Component
@RequiredArgsConstructor
public class UserResolver {
    private final UserClient userClient;

    public UUID resolve(UserCreatePayload createPayload) {
        UserProfile profile = userClient.createNewUser(createPayload);
        return profile.id();
    }

    public void resolve(UUID userId) {
        userClient.existsByUUID(userId);
    }
}
