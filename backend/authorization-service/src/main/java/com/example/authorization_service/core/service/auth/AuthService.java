package com.example.authorization_service.core.service.auth;

import com.example.authorization_service.api.models.AuthCredentials;
import com.example.authorization_service.api.models.AuthResponse;
import com.example.authorization_service.api.models.UserCreatePayload;
import com.example.authorization_service.core.persistence.UserCredentialsRepository;
import com.example.authorization_service.core.persistence.entity.UserLoginCredentials;
import com.example.authorization_service.core.resolver.UserResolver;
import com.example.authorization_service.core.service.session.AccessTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserResolver resolver;
    private final AccessTokenService accessTokenService;
    private final UserCredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse loginUser(AuthCredentials authCredentials) {
        UserLoginCredentials credentials = credentialsRepository.findByLogin(authCredentials.login());
        checkUserExisting(credentials);
        checkPassword(authCredentials, credentials);

        return createSession(credentials);
    }
    //TODO: сетевой запрос в user-service для создания данных о пользователе,
    // затем - сохранение в локальную БД.
    @Transactional
    public AuthResponse registerUser(UserCreatePayload payload) {
        UUID userId = resolver.resolve(payload);
        UserLoginCredentials credentials = createCredentials(userId, payload);
        credentialsRepository.saveCredentials(credentials);

        return createSession(credentials);
    }

    @Transactional
    public void invalidateSession(String accessToken) {
        accessTokenService.invalidateAccessToken(accessToken);
    }

    public void checkSession(String token) {
        accessTokenService.validateAccessToken(token);
    }

    private AuthResponse createSession(UserLoginCredentials credentials) {
        String accessToken = accessTokenService.createAccessToken(credentials.userId());
        return new AuthResponse(accessToken);
    }

    private void checkUserExisting(UserLoginCredentials credentials) {
        resolver.resolve(credentials.userId());
    }

    private void checkPassword(AuthCredentials authCredentials, UserLoginCredentials credentials){
        if(!passwordEncoder.matches(authCredentials.password(), credentials.hashPassword())) throw new IllegalArgumentException("Неверный логин или пароль");
    }

    private UserLoginCredentials createCredentials(UUID userId, UserCreatePayload payload) {
        String hashPassword = passwordEncoder.encode(payload.getPassword());
        return new UserLoginCredentials(userId, payload.getLogin(), hashPassword);
    }
}
