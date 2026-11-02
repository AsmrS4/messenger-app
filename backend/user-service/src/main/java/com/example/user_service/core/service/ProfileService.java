package com.example.user_service.core.service;

import com.example.user_service.api.models.UpdatePayload;
import com.example.user_service.core.models.UserProfile;

import java.util.UUID;

public interface ProfileService {
    UserProfile getCurrentProfile();
    UserProfile updateCurrentUserProfile(UpdatePayload payload);
    UserProfile getProfileByLogin(String login);
    UserProfile getProfileByUUID(UUID userId);
}
