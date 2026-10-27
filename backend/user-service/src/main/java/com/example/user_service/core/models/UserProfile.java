package com.example.user_service.core.models;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record UserProfile(UUID id, String fullName, String email, String login, String phone, LocalDate birthDate, String city, UUID image) {
}
