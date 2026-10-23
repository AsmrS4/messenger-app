package com.example.user_service.api.resolver;

import com.example.user_service.api.models.CreatePayload;
import com.example.user_service.api.models.UpdatePayload;
import com.example.user_service.api.models.UserPayload;
import org.springframework.stereotype.Component;

@Component
public class UserPayloadResolver {
    public void resolve(UserPayload payload) {}

    private void validate(UserPayload payload) {}

    private <T extends UserPayload> T downCastPayload(UserPayload payload) {
        return switch (payload) {
            case CreatePayload create -> (T) create;
            case UpdatePayload update -> (T) update;
            default -> throw new IllegalArgumentException("Неизвестный тип payload: " + payload);
        };
    }
}
