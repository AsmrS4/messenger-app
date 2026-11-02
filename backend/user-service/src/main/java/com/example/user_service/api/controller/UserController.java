package com.example.user_service.api.controller;

import com.example.user_service.api.models.CreatePayload;
import com.example.user_service.api.models.SearchParams;
import com.example.user_service.api.resolver.SearchParamsResolver;
import com.example.user_service.api.resolver.UserPayloadResolver;
import com.example.user_service.core.models.UserProfile;
import com.example.user_service.core.models.Users;
import com.example.user_service.core.service.UserManagement;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springdoc.core.annotations.ParameterObject;
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

    private final Logger logger;

    public UserController(
            @Autowired UserManagement management,
            @Autowired SearchParamsResolver paramsResolver,
            @Autowired UserPayloadResolver userPayloadResolver
    ) {
        this.userManagement = management;
        this.paramsResolver = paramsResolver;
        this.userPayloadResolver = userPayloadResolver;

        this.logger = LogManager.getLogger(UserController.class);
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfile createNewUser(@RequestBody CreatePayload payload) {
        logger.info("Полученные данные от клиента: {}", payload);
        userPayloadResolver.resolveCreationPayload(payload);
        UserProfile profile = userManagement.createNewUser(payload);
        logger.info("Создан новый пользователь: {}", profile);
        return profile;
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Users retrieveUsers(@ParameterObject SearchParams params) {
        SearchParams resolved = paramsResolver.resolve(params);
        return userManagement.findAll(resolved);
    }
}
