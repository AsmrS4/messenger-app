package com.example.user_service.core.service;

import com.example.user_service.api.models.CreatePayload;
import com.example.user_service.api.models.SearchParams;
import com.example.user_service.api.models.UpdateCredentials;
import com.example.user_service.api.models.UpdatePayload;
import com.example.user_service.core.http.AuthenticationClient;
import com.example.user_service.core.models.Pagination;
import com.example.user_service.core.models.UserProfile;
import com.example.user_service.core.models.Users;
import com.example.user_service.core.persistence.User;
import com.example.user_service.core.persistence.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserManagement, ProfileService {
    private final static int LIMIT = 20;
    private final UserRepo repository;
    private final AuthenticationClient client;

    @Override
    @Transactional
    public UserProfile createNewUser(CreatePayload payload) {
        checkUniqueness(payload);

        User newUser = User.builder()
                .fullName(payload.getFullName())
                .email(payload.getEmail())
                .login(payload.getLogin())
                .phone(payload.getPhone())
                .birthDate(payload.getBirthDate())
                .city(payload.getCity())
                .build();

        UUID userId = repository.saveUser(newUser);

        return repository.findById(userId);
    }

    @Override
    @Transactional
    public UserProfile updateCurrentUserProfile(UpdatePayload payload) {
        UUID currentUserId = getCurrentUser().id();
        repository.updateUser(currentUserId, payload);
        client.updateCredentials(new UpdateCredentials(currentUserId, payload.getLogin()));
        return getProfileByUUID(currentUserId);
    }

    @Override
    public UserProfile getCurrentProfile() {
        return getCurrentUser();
    }

    @Override
    public UserProfile getProfileByLogin(String login) {
        if(!repository.existsByLogin(login)) throw new NoSuchElementException("Пользователь не найден");
        return repository.findByLogin(login);
    }

    @Override
    public UserProfile getProfileByUUID(UUID userId) {
        UserProfile user = repository.findById(userId);
        if(user == null) throw new NoSuchElementException("Пользователь не найден");
        return user;
    }

    @Override
    public Users findAll(SearchParams params) {
        LocalDate minAgeDateBound = LocalDate.now().minusYears(params.getMaxAge());
        LocalDate maxAgeDateBound = LocalDate.now().minusYears(params.getMinAge());

        List<UserProfile> users = repository.findAllWithParams(params, minAgeDateBound, maxAgeDateBound, params.getPage() * LIMIT);
        if (users.isEmpty()) return new Users(users, new Pagination(params.getPage() + 1, LIMIT, 0));

        long totalCount = repository.countTotal(params, minAgeDateBound, maxAgeDateBound);
        return new Users(users, new Pagination(params.getPage() + 1, LIMIT, (int) totalCount));
    }

    private void checkUniqueness(CreatePayload payload) {
        if(repository.existsByLogin(payload.getLogin()))
            throw new IllegalArgumentException("Логин %s занят".formatted(payload.getLogin()));

        if(repository.existsByEmail(payload.getEmail()))
            throw new IllegalArgumentException("Email %s занят".formatted(payload.getEmail()));

        if(repository.existsByPhone(payload.getPhone()))
            throw new IllegalArgumentException("Номер %s занят".formatted(payload.getPhone()));
    }

    private UserProfile getCurrentUser() {
        try {
            UUID userId = (UUID) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
            return getProfileByUUID(userId);
        } catch (Exception ex) {
            throw new AuthenticationServiceException("Вы не авторизованы");
        }
    }
}
