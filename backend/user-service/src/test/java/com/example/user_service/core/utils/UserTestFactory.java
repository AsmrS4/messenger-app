package com.example.user_service.core.utils;

import com.example.user_service.api.models.CreatePayload;
import com.example.user_service.core.persistence.User;
import com.example.user_service.core.persistence.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserTestFactory {
    private UserTestFactory() {
    }

    public static UUID createUser(UserRepo userRepo, CreatePayload payload) {
        User newUser = User.builder()
                .fullName(payload.getFullName())
                .login(payload.getLogin())
                .email(payload.getEmail())
                .phone(payload.getPhone())
                .birthDate(payload.getBirthDate())
                .city(payload.getCity())
                .build();
        return userRepo.saveUser(newUser);
    }
}
