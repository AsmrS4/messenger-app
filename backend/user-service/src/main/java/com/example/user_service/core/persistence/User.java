package com.example.user_service.core.persistence;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/*
* Поля email, login, phone - должны быть уникальными
* */

@Builder
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
