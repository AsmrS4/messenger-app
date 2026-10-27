package com.example.user_service.core.models;

import java.util.List;

public record Users(List<UserProfile> users, Pagination pagination) {
}
