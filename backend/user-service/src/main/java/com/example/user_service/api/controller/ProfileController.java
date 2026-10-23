package com.example.user_service.api.controller;

import com.example.user_service.api.models.UpdatePayload;
import com.example.user_service.core.models.UserProfile;
import com.example.user_service.core.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/profile")
@ResponseStatus(HttpStatus.OK)
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(@Autowired ProfileService service) {
        this.profileService = service;
    }

    @PutMapping("/me")
    public UserProfile updateUser(UpdatePayload payload) {
        return profileService.updateCurrentUserProfile(payload);
    }

    @GetMapping("/me")
    public UserProfile retrieveCurrentUserProfile() {
        return profileService.getCurrentProfile();
    }

    @GetMapping("/find")
    public UserProfile retrieveUserProfileByLogin(@RequestParam String login) {
        return profileService.getProfileByLogin(login);
    }

    @GetMapping("/{userId}")
    public UserProfile retrieveUserByUUID(@PathVariable UUID userId) {
        return profileService.getProfileByUUID(userId);
    }
}
