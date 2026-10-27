package com.example.user_service.api.controller;

import com.example.user_service.api.models.CreatePayload;
import com.example.user_service.api.models.SearchParams;
import com.example.user_service.api.resolver.SearchParamsResolver;
import com.example.user_service.api.resolver.UserPayloadResolver;
import com.example.user_service.core.models.UserProfile;
import com.example.user_service.core.models.Users;
import com.example.user_service.core.service.UserManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@ResponseStatus(HttpStatus.OK)
public class UserController {
    private final UserManagement userManagement;
    private final SearchParamsResolver paramsResolver;
    private final UserPayloadResolver userPayloadResolver;

    public UserController(
            @Autowired UserManagement management,
            @Autowired SearchParamsResolver paramsResolver,
            @Autowired UserPayloadResolver userPayloadResolver
    ) {
        this.userManagement = management;
        this.paramsResolver = paramsResolver;
        this.userPayloadResolver = userPayloadResolver;
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfile createNewUser(CreatePayload payload) {
        userPayloadResolver.resolveCreationPayload(payload);
        return userManagement.createNewUser(payload);
    }

    @GetMapping
    public Users retrieveUsers(SearchParams params) {
        SearchParams resolved = paramsResolver.resolve(params);
        return userManagement.findAll(resolved);
    }
}
