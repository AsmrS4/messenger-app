package com.example.authorization_service.core.resolver;

import com.example.authorization_service.api.models.UserCreatePayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

/*
*
* Валидация параметров лежит на User-service
* */

@Component
public class UserResolver {
    public UserResolver() {}

    public UUID resolve(UserCreatePayload createPayload) {return null;}

    public void resolve(UUID userId) {}
}
