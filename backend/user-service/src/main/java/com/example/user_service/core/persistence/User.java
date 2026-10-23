package com.example.user_service.core.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record User(
        String fullName,
        String email,
        String login,
        String phone,
        LocalDate birthDate,
        String city,
        UUID imageId,
        LocalDateTime createTime
) {
}
