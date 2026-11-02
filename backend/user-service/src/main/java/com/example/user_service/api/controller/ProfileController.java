package com.example.user_service.api.controller;

import com.example.user_service.api.models.UpdatePayload;
import com.example.user_service.api.resolver.UserPayloadResolver;
import com.example.user_service.core.models.UserProfile;
import com.example.user_service.core.service.ProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/profile")
@ResponseStatus(HttpStatus.OK)
public class ProfileController {
    private final UserPayloadResolver userPayloadResolver;
    private final ProfileService profileService;

    private final Logger logger;

    public ProfileController(@Autowired ProfileService service, @Autowired UserPayloadResolver userPayloadResolver) {
        this.profileService = service;
        this.userPayloadResolver = userPayloadResolver;
        this.logger = LogManager.getLogger(ProfileController.class);
        System.out.println("Класс логгера: " + logger.getClass().getName());
    }

    @PutMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public UserProfile updateUser(@RequestBody UpdatePayload payload) {
        userPayloadResolver.resolveUpdatePayload(payload);
        return profileService.updateCurrentUserProfile(payload);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public UserProfile retrieveCurrentUserProfile() {
        UserProfile currentUser = profileService.getCurrentProfile();
        logger.info("Текущий авторизованный пользователь: {}", currentUser);
        return currentUser;
    }

    @GetMapping("/find")
    @SecurityRequirement(name = "bearerAuth")
    public UserProfile retrieveUserProfileByLogin(@RequestParam String login) {
        return profileService.getProfileByLogin(login);
    }

    @GetMapping("/{userId}")
    public UserProfile retrieveUserByUUID(@PathVariable UUID userId) {
        return profileService.getProfileByUUID(userId);
    }
}
