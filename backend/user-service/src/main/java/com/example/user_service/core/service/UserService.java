package com.example.user_service.core.service;

import com.example.user_service.api.models.CreatePayload;
import com.example.user_service.api.models.SearchParams;
import com.example.user_service.api.models.UpdatePayload;
import com.example.user_service.core.models.UserProfile;
import com.example.user_service.core.models.Users;
import com.example.user_service.core.persistence.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService implements UserManagement, ProfileService {
    private final UserRepo repository;

    public UserService(@Autowired UserRepo repo) {
        this.repository = repo;
    }

    public UserProfile createNewUser() {
        return null;
    }

    @Override
    public UserProfile getCurrentProfile() {
        return null;
    }

    @Override
    public UserProfile updateCurrentUserProfile(UpdatePayload payload) {
        return null;
    }

    @Override
    public UserProfile getProfileByLogin(String login) {
        return null;
    }

    @Override
    public UserProfile getProfileByUUID(UUID userId) {
        return null;
    }

    @Override
    public UserProfile createNewUser(CreatePayload payload) {
        return null;
    }

    @Override
    public Users findAll(SearchParams params) {
        return null;
    }
}
