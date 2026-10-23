package com.example.user_service.core.utils;

import com.example.user_service.core.models.UserProfile;
import com.example.user_service.core.persistence.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserProfile mapToResponse(User user) {
        return UserProfile.builder()
                .fullName(user.fullName())
                .email(user.email())
                .phone(user.phone())
                .birthDate(user.birthDate())
                .city(user.city())
                .image(user.imageId())
                .build();
    }
}
