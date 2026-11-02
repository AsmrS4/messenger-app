package com.example.authorization_service.core.service.session;

import java.util.UUID;

public interface AccessTokenService {
    String createAccessToken(UUID userId);
    void validateAccessToken(String token);
    void invalidateAccessToken(String token);
}
