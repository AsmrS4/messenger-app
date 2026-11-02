package com.example.authorization_service.core.persistence.entity;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserLoginCredentials(UUID userId, String login, String hashPassword) {
}
