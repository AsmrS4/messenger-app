package com.example.user_service.api.resolver;

import com.example.user_service.api.models.CreatePayload;
import com.example.user_service.api.models.UpdatePayload;
import com.example.user_service.api.models.UserPayload;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserPayloadResolver {
    private final static String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}$";
    private final static String PHONE_REGEX = "^\\+7[\\s-]?\\(?\\d{3}\\)?[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2}$";
    private final static String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d).+$";
    private final static String PASSWORD_PERMITTED_ALPHABET_REGEX = "^[a-zA-Z0-9]+$";

    public void resolveUpdatePayload(UpdatePayload payload) {
        validate(payload);
    }

    public void resolveCreationPayload(CreatePayload payload) {
        validate(payload);
        validateCreationPayload(payload);
    }

    private void validate(UserPayload payload) {
        if(payload.getFullName() == null || payload.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Параметр \"ФИО\" обязателен к заполнению.");
        }
        if(payload.getFullName().trim().length() > 255) {
            throw new IllegalArgumentException("Допустимая длина строки параметра \"ФИО\" - 255 символов.");
        }
        if(payload.getLogin() == null || payload.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Параметр \"login\" обязателен к заполнению.");
        }
        if(payload.getLogin().trim().length() > 100) {
            throw new IllegalArgumentException("Допустимая длина строки параметра \"login\" - 100 символов.");
        }
        if(payload.getEmail() != null && !payload.getEmail().trim().matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Невалидный формат email.");
        }
        if(payload.getPhone() !=null && !payload.getPhone().trim().matches(PHONE_REGEX)) {
            throw new IllegalArgumentException("Невалидный формат номера телефона");
        }
        if(payload.getBirthDate() != null &&  payload.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата рождения не может быть раньше сегодняшнего дня");
        }
    }

    private void validateCreationPayload(CreatePayload createPayload) {
        validatePassword(createPayload);
    }

    private void validatePassword(CreatePayload payload) {
        if(payload.getPassword() == null || payload.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль обязателен к заполнению.");
        }
        if(payload.getPassword().trim().length() < 8) {
            throw new IllegalArgumentException("Длина пароля должна быть более 8 символов");
        }
        if(!payload.getPassword().matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException("Пароль должен содержать буквы и цифры");
        }
        if(!payload.getPassword().matches(PASSWORD_PERMITTED_ALPHABET_REGEX)) {
            throw new IllegalArgumentException("Пароль может содержать только латинские буквы");
        }
    }

    private <T extends UserPayload> T downCastPayload(UserPayload payload, Class<T> clazz) {
        if (clazz.isInstance(payload)) {
            return clazz.cast(payload);
        }
        throw new IllegalArgumentException("Неизвестный тип payload: " + payload.getClass().getName());
    }
}
