package com.example.user_service.core.service;

import com.example.user_service.api.models.CreatePayload;
import com.example.user_service.api.models.SearchParams;
import com.example.user_service.core.models.UserProfile;
import com.example.user_service.core.models.Users;

public interface UserManagement {
    UserProfile createNewUser(CreatePayload payload);
    Users findAll(SearchParams params);
}
