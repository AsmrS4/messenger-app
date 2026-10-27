package com.example.user_service.core.persistence;

import com.example.user_service.api.models.CreatePayload;
import com.example.user_service.api.models.SearchParams;
import com.example.user_service.api.models.UpdatePayload;
import com.example.user_service.configuration.TestBeansConfiguration;
import com.example.user_service.core.models.UserProfile;
import com.example.user_service.core.utils.UserTestFactory;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Import(TestBeansConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@RequiredArgsConstructor
public class UserRepoTest {
    private final UserRepo repo;
    private final Jdbi jdbi;
    private static List<CreatePayload> createUserPayloads;
    private static final int LIMIT = 20;

    @BeforeEach
    public void cleanDatabase() {
        jdbi.useHandle(handle -> handle.execute("TRUNCATE TABLE users CASCADE"));
    }

    @BeforeAll
    public static void setUp() {
        CreatePayload newUser1 = CreatePayload.builder()
                .fullName("User1")
                .login("test_user1")
                .email("user1@example.com")
                .phone("+79005553535")
                .city("Tomsk")
                .birthDate(LocalDate.parse("2000-01-01"))
                .password("password123")
                .build();
        CreatePayload newUser2 = CreatePayload.builder()
                .fullName("User2")
                .login("test_user2")
                .email("user2@example.com")
                .phone("+79005553536")
                .city("Tomsk")
                .birthDate(LocalDate.parse("2000-01-01"))
                .password("password123")
                .build();
        CreatePayload newUser3 = CreatePayload.builder()
                .fullName("User3")
                .login("test_user3")
                .email("user3@example.com")
                .phone("+79005553537")
                .city("Novosibirsk")
                .birthDate(LocalDate.parse("2000-01-01"))
                .password("password123")
                .build();
        createUserPayloads = List.of(newUser1, newUser2, newUser3);
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
    @DisplayName("Проверка на получение несуществующего пользователя в системе")
    public void shouldReturnNullWithNotExistingUser() {
        UUID userId = UUID.randomUUID();

        UserProfile user = repo.findById(userId);

        assertThat(user).isNull();
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

    @Test
    @DisplayName("Проверка на занятый логин")
    public void shouldReturnTrueWithOccupiedLogin() {
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
        boolean exists = repo.existsByLogin("test_user");

        assertThat(id).isNotNull();
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Проверка на незанятый логин")
    public void shouldReturnFalseWithNotOccupiedLogin() {
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
        boolean exists = repo.existsByLogin("test_user2");

        assertThat(id).isNotNull();
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Проверка на занятый email")
    public void shouldReturnTrueWithOccupiedEmail() {
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
        boolean exists = repo.existsByEmail("user@example.com");

        assertThat(id).isNotNull();
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Проверка на незанятый email")
    public void shouldReturnFalseWithNotOccupiedEmail() {
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
        boolean exists = repo.existsByEmail("user2@example.com");

        assertThat(id).isNotNull();
        assertThat(exists).isFalse();

    }

    @Test
    @DisplayName("Проверка на занятый номер телефона")
    public void shouldReturnTrueWithOccupiedPhone() {
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
        boolean exists = repo.existsByPhone("+79005553535");

        assertThat(id).isNotNull();
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Проверка на незанятый номер телефона")
    public void shouldReturnFalseWithNotOccupiedPhone() {
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
        boolean exists = repo.existsByPhone("+79005553536");

        assertThat(id).isNotNull();
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Проверка нахождения списка пользователей с параметрами по умолчанию")
    public void shouldReturnUsers() {
        createUserPayloads.forEach(payload -> UserTestFactory.createUser(repo, payload));
        SearchParams params = new SearchParams();

        LocalDate minAgeDateBound = LocalDate.now().minusYears(params.getMaxAge());
        LocalDate maxAgeDateBound = LocalDate.now().minusYears(params.getMinAge());

        List<UserProfile> foundUsers = repo.findAllWithParams(params, minAgeDateBound, maxAgeDateBound, (params.getPage() - 1) * LIMIT);

        assertThat(foundUsers).isNotNull();
        assertThat(foundUsers.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Проверка нахождения списка пользователей по совпадению ФИО")
    public void shouldReturnUsersByFullNameCoincidence() {
        createUserPayloads.forEach(payload -> UserTestFactory.createUser(repo, payload));
        SearchParams params = new SearchParams();
        params.setSearchName("User1");

        LocalDate minAgeDateBound = LocalDate.now().minusYears(params.getMaxAge());
        LocalDate maxAgeDateBound = LocalDate.now().minusYears(params.getMinAge());

        List<UserProfile> foundUsers = repo.findAllWithParams(params, minAgeDateBound, maxAgeDateBound, (params.getPage() - 1) * LIMIT);

        assertThat(foundUsers).isNotNull();
        assertThat(foundUsers.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Проверка на пустой список при не совпадении ФИО")
    public void shouldReturnEmptyUsersWithNoFullNameCoincidence() {
        createUserPayloads.forEach(payload -> UserTestFactory.createUser(repo, payload));
        SearchParams params = new SearchParams();
        params.setSearchName("User123");

        LocalDate minAgeDateBound = LocalDate.now().minusYears(params.getMaxAge());
        LocalDate maxAgeDateBound = LocalDate.now().minusYears(params.getMinAge());

        List<UserProfile> foundUsers = repo.findAllWithParams(params, minAgeDateBound, maxAgeDateBound, (params.getPage() - 1) * LIMIT);

        assertThat(foundUsers).isNotNull();
        assertThat(foundUsers.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Проверка нахождения списка пользователей по совпадению города")
    public void shouldReturnUsersByCityCoincidence() {
        createUserPayloads.forEach(payload -> UserTestFactory.createUser(repo, payload));
        SearchParams params = new SearchParams();
        params.setCity("Novosibirsk");

        LocalDate minAgeDateBound = LocalDate.now().minusYears(params.getMaxAge());
        LocalDate maxAgeDateBound = LocalDate.now().minusYears(params.getMinAge());

        List<UserProfile> foundUsers = repo.findAllWithParams(params, minAgeDateBound, maxAgeDateBound, (params.getPage() - 1) * LIMIT);

        assertThat(foundUsers).isNotNull();
        assertThat(foundUsers.size()).isEqualTo(1);
        assertThat(foundUsers.getFirst().fullName()).isEqualTo("User3");
    }

    @Test
    @DisplayName("Проверка на пустой список при не совпадении города")
    public void shouldReturnEmptyUsersWithNoCityCoincidence() {
        createUserPayloads.forEach(payload -> UserTestFactory.createUser(repo, payload));
        SearchParams params = new SearchParams();
        params.setCity("Moscow");

        LocalDate minAgeDateBound = LocalDate.now().minusYears(params.getMaxAge());
        LocalDate maxAgeDateBound = LocalDate.now().minusYears(params.getMinAge());

        List<UserProfile> foundUsers = repo.findAllWithParams(params, minAgeDateBound, maxAgeDateBound, (params.getPage() - 1) * LIMIT);

        assertThat(foundUsers).isNotNull();
        assertThat(foundUsers.size()).isEqualTo(0);
    }
}
