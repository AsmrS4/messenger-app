package com.example.authorization_service.api.models;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
public class UserCreatePayload {
    private String fullName;
    private String login;
    private String email;
    private String phone;
    private String city;
    private String password;
    private LocalDate birthDate;
}