package com.example.user_service.api.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreatePayload extends UserPayload{
    private String password;
}
