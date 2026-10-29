package com.example.user_service.core.http;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AuthValidationInterceptor implements RequestInterceptor {
    private AuthenticationClient authClient;

    public AuthValidationInterceptor(AuthenticationClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new AuthorizationServiceException("Отсутствует заголовок авторизации");

        try {
            authClient.checkSession(authHeader);
        } catch (Exception e) {
            throw new AuthorizationServiceException("Token validation failed");
        }

        template.header("Authorization", authHeader);
    }
}