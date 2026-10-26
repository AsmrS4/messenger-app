package com.example.user_service.core.persistence;

import com.example.user_service.api.models.UpdatePayload;
import com.example.user_service.configuration.TestBeansConfiguration;
import com.example.user_service.core.models.UserProfile;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Import(TestBeansConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class UserRepoTest {
    private final UserRepo repo;
    private final Jdbi jdbi;

    @BeforeEach
    void cleanDatabase() {
        jdbi.useHandle(handle -> handle.execute("TRUNCATE TABLE users CASCADE"));
    }

    @Test
    @DisplayName("Проверка сохранения нового пользователя в БД")
    public void shouldSaveUserSuccessfully() {
        User newUser = new User(
                "User",
                "user@example.com",
                "test_user",
                "+79005553535",
                LocalDate.parse("2000-01-01"),
                "Tomsk",
                null,
                LocalDateTime.now()
        );

        UUID id = repo.saveUser(newUser);

        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("Проверка на генерацию уникальных id для пользователей")
    public void shouldGenerateUniqueUUIDForUsers() {
        User newUserFirst = new User(
                "User",
                "user@example.com",
                "test_user",
                "+79005553535",
                LocalDate.parse("2000-01-01"),
                "Tomsk",
                null,
                LocalDateTime.now()
        );

        User newUserSecond = new User(
                "User",
                "user2@example.com",
                "test_user2",
                "+79005553536",
                LocalDate.parse("2000-01-01"),
                "Tomsk",
                null,
                LocalDateTime.now()
        );

        UUID firstUserId = repo.saveUser(newUserFirst);
        UUID secondUserId = repo.saveUser(newUserSecond);

        assertNotEquals(firstUserId, secondUserId);
    }

    @Test
    @DisplayName("Проверка получения пользователя по id из БД")
    public void shouldFindSavedUserByIdSuccessfully() {
        User newUser = new User(
                "User",
                "user@example.com",
                "test_user",
                "+79005553535",
                LocalDate.parse("2000-01-01"),
                "Tomsk",
                null,
                LocalDateTime.now()
        );

        UUID id = repo.saveUser(newUser);
        UserProfile user = repo.findById(id);

        assertEquals(id, user.id());
        assertEquals(newUser.fullName(), user.fullName());
        assertEquals(newUser.login(), user.login());
        assertEquals(newUser.phone(), user.phone());
    }

    @Test
    @DisplayName("Проверка получения пользователя по логину из БД")
    public void shouldFindSavedUserByLoginSuccessfully() {
        User newUser = new User(
                "User",
                "user@example.com",
                "test_user",
                "+79005553535",
                LocalDate.parse("2000-01-01"),
                "Tomsk",
                null,
                LocalDateTime.now()
        );

        UUID id = repo.saveUser(newUser);
        UserProfile user = repo.findByLogin("test_user");

        assertEquals(id, user.id());
        assertEquals(newUser.fullName(), user.fullName());
        assertEquals(newUser.login(), user.login());
        assertEquals(newUser.phone(), user.phone());
    }

    @Test
    @DisplayName("Проверка обновления информации о пользователе")
    public void shouldUpdateUserInfoSuccessfully() {
        User newUser = new User(
                "User",
                "user@example.com",
                "test_user",
                "+79005553535",
                LocalDate.parse("2000-01-01"),
                "Tomsk",
                null,
                LocalDateTime.now()
        );

        UpdatePayload payload = UpdatePayload.builder()
                .fullName("User2")
                .email("user2@example.com")
                .login("test_user2")
                .phone("+79005553535")
                .birthDate(LocalDate.parse("2000-01-02"))
                .city("Tomsk")
                .build();

        UUID id = repo.saveUser(newUser);
        int updatedRows = repo.updateUser(id, payload);

        assertThat(updatedRows).isEqualTo(1);
    }
}
