package com.example.user_service.api.models;

import java.util.UUID;

public record UpdateCredentials(UUID userId, String login) {
}
