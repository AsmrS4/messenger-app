package com.example.user_service.api.resolver;

import com.example.user_service.api.models.UserPayload;
import org.springframework.stereotype.Component;

@Component
public class UserPayloadResolver {
    public void resolve(UserPayload payload) {}

    private void validate(UserPayload payload) {}

    private <T extends UserPayload> T downCastPayload(UserPayload payload, Class<T> clazz) {
        if (clazz.isInstance(payload)) {
            return clazz.cast(payload);
        }
        throw new IllegalArgumentException("Неизвестный тип payload: " + payload.getClass().getName());
    }
}
