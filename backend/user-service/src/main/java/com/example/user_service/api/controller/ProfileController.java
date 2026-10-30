package com.example.user_service.api.controller;

import com.example.user_service.api.models.UpdatePayload;
import com.example.user_service.api.resolver.UserPayloadResolver;
import com.example.user_service.core.models.UserProfile;
import com.example.user_service.core.service.ProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    public ProfileController(@Autowired ProfileService service, @Autowired UserPayloadResolver userPayloadResolver) {
        this.profileService = service;
        this.userPayloadResolver = userPayloadResolver;
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
        return profileService.getCurrentProfile();
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
