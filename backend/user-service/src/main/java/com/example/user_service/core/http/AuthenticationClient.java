package com.example.user_service.core.http;

public interface AuthenticationClient {
    void checkSession(String authToken);
}
