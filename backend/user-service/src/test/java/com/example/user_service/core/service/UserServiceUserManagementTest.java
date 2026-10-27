package com.example.user_service.core.service;

import com.example.user_service.api.models.CreatePayload;
import com.example.user_service.api.models.SearchParams;
import com.example.user_service.core.models.UserProfile;
import com.example.user_service.core.models.Users;
import com.example.user_service.core.persistence.User;
import com.example.user_service.core.persistence.UserRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUserManagementTest {
    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    private static CreatePayload userPayload;
    private static List<UserProfile> users;
    private static final int LIMIT = 20;

    @BeforeAll
    public static void setUp() {
        userPayload = CreatePayload.builder()
                .fullName("User")
                .login("test_user")
                .email("user@example.com")
                .phone("+79005553535")
                .city("Tomsk")
                .birthDate(LocalDate.parse("2000-01-01"))
                .password("password123")
                .build();
        UserProfile newUser1 = UserProfile.builder()
                .fullName("User1")
                .login("test_user1")
                .email("user1@example.com")
                .phone("+79005553535")
                .city("Tomsk")
                .birthDate(LocalDate.parse("2000-01-01"))
                .build();
        UserProfile newUser2 = UserProfile.builder()
                .fullName("User2")
                .login("test_user2")
                .email("user2@example.com")
                .phone("+79005553536")
                .city("Tomsk")
                .birthDate(LocalDate.parse("2000-01-01"))
                .build();
        UserProfile newUser3 = UserProfile.builder()
                .fullName("User3")
                .login("test_user3")
                .email("user3@example.com")
                .phone("+79005553537")
                .city("Novosibirsk")
                .birthDate(LocalDate.parse("2000-01-01"))
                .build();
        users = List.of(newUser1, newUser2, newUser3);
    }

    @Test
    @DisplayName("Проверка на успешное создание и сохранение пользователя в БД")
    public void shouldCreateNewUserSuccessfully() {
        User newUser = User.builder()
                .fullName(userPayload.getFullName())
                .email(userPayload.getEmail())
                .login(userPayload.getLogin())
                .phone(userPayload.getPhone())
                .birthDate(userPayload.getBirthDate())
                .city(userPayload.getCity())
                .build();
        UUID userId = UUID.randomUUID();
        when(userRepo.saveUser(newUser)).thenReturn(userId);

        userService.createNewUser(userPayload);

        verify(userRepo, times(1)).saveUser(newUser);
        assertThat(userId).isNotNull();
    }

    @Test
    @DisplayName("Проверка на получение существующего пользователя в системе.")
    public void shouldReturnUserProfileAfterSaving() {
        User newUser = User.builder()
                .fullName(userPayload.getFullName())
                .email(userPayload.getEmail())
                .login(userPayload.getLogin())
                .phone(userPayload.getPhone())
                .birthDate(userPayload.getBirthDate())
                .city(userPayload.getCity())
                .build();
        UUID userId = UUID.randomUUID();

        UserProfile profile = UserProfile.builder()
                .id(userId)
                .fullName(userPayload.getFullName())
                .email(userPayload.getEmail())
                .login(userPayload.getLogin())
                .phone(userPayload.getPhone())
                .city(userPayload.getCity())
                .birthDate(userPayload.getBirthDate())
                .build();


        when(userRepo.saveUser(newUser)).thenReturn(userId);
        when(userRepo.findById(userId)).thenReturn(profile);

        UserProfile userProfile = userService.createNewUser(userPayload);

        verify(userRepo, times(1)).saveUser(newUser);
        verify(userRepo, times(1)).findById(userId);

        assertEquals(userProfile.id(), profile.id());
        assertEquals(userProfile.fullName(), profile.fullName());
        assertEquals(userProfile.login(), profile.login());
        assertEquals(userProfile.phone(), profile.phone());
        assertEquals(userProfile.email(), profile.email());
    }

    @Test
    @DisplayName("Проверка на получение несуществующего пользователя по uuid")
    public void shouldThrowNoSuchElementExceptionWithNotExistingUserByUUID() {
        UUID userId = UUID.randomUUID();
        when(userRepo.findById(userId)).thenReturn(null);

        Exception ex = assertThrows(NoSuchElementException.class, () -> {
            userService.getProfileByUUID(userId);
        });

        assertEquals("Пользователь не найден", ex.getMessage());
        verify(userRepo, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Проверка на получение несуществующего пользователя по логину")
    public void shouldThrowNoSuchElementExceptionWithNotExistingUserByLogin() {
        String login = "some_login";

        Exception ex = assertThrows(NoSuchElementException.class, () -> {
            userService.getProfileByLogin(login);
        });

        assertEquals("Пользователь не найден", ex.getMessage());
        verify(userRepo, times(1)).existsByLogin(login);
        verify(userRepo, never()).findByLogin(login);
    }

    @Test
    @DisplayName("Проверка на попытку передать не уникальный логин")
    public void shouldThrowIllegalArgumentExceptionWithOccupiedLogin() {
        User newUser = User.builder()
                .fullName(userPayload.getFullName())
                .login(userPayload.getLogin())
                .email(userPayload.getEmail())
                .phone(userPayload.getPhone())
                .city(userPayload.getCity())
                .birthDate(userPayload.getBirthDate())
                .build();

        when(userRepo.existsByLogin(userPayload.getLogin())).thenReturn(true);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.createNewUser(userPayload);
        });

        assertEquals("Логин %s занят".formatted(userPayload.getLogin()), ex.getMessage());
        verify(userRepo, never()).saveUser(newUser);
        verify(userRepo, never()).findById(UUID.randomUUID());
    }

    @Test
    @DisplayName("Проверка на попытку передать не уникальный email")
    public void shouldThrowIllegalArgumentExceptionWithOccupiedEmail() {
        User newUser = User.builder()
                .fullName(userPayload.getFullName())
                .login(userPayload.getLogin())
                .email(userPayload.getEmail())
                .phone(userPayload.getPhone())
                .city(userPayload.getCity())
                .birthDate(userPayload.getBirthDate())
                .build();

        when(userRepo.existsByEmail(newUser.email())).thenReturn(true);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.createNewUser(userPayload);
        });

        assertEquals("Email %s занят".formatted(userPayload.getEmail()), ex.getMessage());
        verify(userRepo, never()).saveUser(newUser);
        verify(userRepo, never()).findById(UUID.randomUUID());
    }

    @Test
    @DisplayName("Проверка на попытку передать не уникальный номер телефона")
    public void shouldThrowIllegalArgumentExceptionWithOccupiedPhone() {
        User newUser = User.builder()
                .fullName(userPayload.getFullName())
                .login(userPayload.getLogin())
                .email(userPayload.getEmail())
                .phone(userPayload.getPhone())
                .city(userPayload.getCity())
                .birthDate(userPayload.getBirthDate())
                .build();

        when(userRepo.existsByPhone(userPayload.getPhone())).thenReturn(true);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.createNewUser(userPayload);
        });

        assertEquals("Номер %s занят".formatted(userPayload.getPhone()), ex.getMessage());
        verify(userRepo, never()).saveUser(newUser);
        verify(userRepo, never()).findById(UUID.randomUUID());
    }

    @Test
    @DisplayName("Проверка нахождения списка пользователей с параметрами по умолчанию")
    public void shouldReturnUsersWithDefaultSearchParams() {
        SearchParams params = new SearchParams();
        LocalDate minAgeDateBound = LocalDate.now().minusYears(params.getMaxAge());
        LocalDate maxAgeDateBound = LocalDate.now().minusYears(params.getMinAge());
        int offset = params.getPage() * LIMIT;

        when(userRepo.findAllWithParams(params, minAgeDateBound, maxAgeDateBound, offset)).thenReturn(users);
        when(userRepo.countTotal(params, minAgeDateBound, maxAgeDateBound)).thenReturn((long) users.size());
        Users userRes = userService.findAll(params);

        assertThat(userRes).isNotNull();
        assertThat(userRes.users()).isNotNull();
        assertThat(userRes.users().size()).isEqualTo(3);
        assertThat(userRes.pagination()).isNotNull();
        assertThat(userRes.pagination().total()).isEqualTo(3);
        assertThat(userRes.pagination().size()).isEqualTo(LIMIT);
        assertThat(userRes.pagination().page()).isEqualTo(params.getPage() + 1);

        verify(userRepo, times(1)).findAllWithParams(params, minAgeDateBound, maxAgeDateBound, offset);
        verify(userRepo, times(1)).countTotal(params, minAgeDateBound, maxAgeDateBound);
    }

    @Test
    @DisplayName("Проверка на получение пустого списка, при отсутсвии пользователей")
    public void shouldReturnEmptyUserList() {
        SearchParams params = new SearchParams();
        LocalDate minAgeDateBound = LocalDate.now().minusYears(params.getMaxAge());
        LocalDate maxAgeDateBound = LocalDate.now().minusYears(params.getMinAge());
        int offset = params.getPage() * LIMIT;

        when(userRepo.findAllWithParams(params, minAgeDateBound, maxAgeDateBound, offset)).thenReturn(List.of());
        Users userRes = userService.findAll(params);

        assertThat(userRes).isNotNull();
        assertThat(userRes.users()).isNotNull();
        assertThat(userRes.users().size()).isEqualTo(0);
        assertThat(userRes.pagination()).isNotNull();
        assertThat(userRes.pagination().total()).isEqualTo(0);
        assertThat(userRes.pagination().size()).isEqualTo(LIMIT);
        assertThat(userRes.pagination().page()).isEqualTo(params.getPage() + 1);

        verify(userRepo, times(1)).findAllWithParams(params, minAgeDateBound, maxAgeDateBound, offset);
        verify(userRepo, never()).countTotal(params, minAgeDateBound, maxAgeDateBound);
    }

    @Test
    @DisplayName("Проверка нахождения списка пользователей по совпадению ФИО")
    public void shouldReturnUsersByFullNameCoincidence() {
        SearchParams params = new SearchParams();
        params.setSearchName("User1");
        LocalDate minAgeDateBound = LocalDate.now().minusYears(params.getMaxAge());
        LocalDate maxAgeDateBound = LocalDate.now().minusYears(params.getMinAge());
        int offset = params.getPage() * LIMIT;

        when(userRepo.findAllWithParams(params, minAgeDateBound, maxAgeDateBound, offset)).thenReturn(List.of(users.getFirst()));
        when(userRepo.countTotal(params, minAgeDateBound, maxAgeDateBound)).thenReturn(1L);
        Users userRes = userService.findAll(params);

        assertThat(userRes).isNotNull();
        assertThat(userRes.users()).isNotNull();
        assertThat(userRes.users().size()).isEqualTo(1);
        assertThat(userRes.pagination()).isNotNull();
        assertThat(userRes.pagination().total()).isEqualTo(1);
        assertThat(userRes.pagination().size()).isEqualTo(LIMIT);
        assertThat(userRes.pagination().page()).isEqualTo(params.getPage() + 1);

        verify(userRepo, times(1)).findAllWithParams(params, minAgeDateBound, maxAgeDateBound, offset);
        verify(userRepo, times(1)).countTotal(params, minAgeDateBound, maxAgeDateBound);
    }

    @Test
    @DisplayName("Проверка на пустой список при не совпадении ФИО")
    public void shouldReturnEmptyUsersWithNoFullNameCoincidence(){
        SearchParams params = new SearchParams();
        params.setSearchName("User1");
        LocalDate minAgeDateBound = LocalDate.now().minusYears(params.getMaxAge());
        LocalDate maxAgeDateBound = LocalDate.now().minusYears(params.getMinAge());
        int offset = params.getPage() * LIMIT;

        when(userRepo.findAllWithParams(params, minAgeDateBound, maxAgeDateBound, offset)).thenReturn(List.of());
        Users userRes = userService.findAll(params);

        assertThat(userRes).isNotNull();
        assertThat(userRes.users()).isNotNull();
        assertThat(userRes.users().size()).isEqualTo(0);
        assertThat(userRes.pagination()).isNotNull();
        assertThat(userRes.pagination().total()).isEqualTo(0);
        assertThat(userRes.pagination().size()).isEqualTo(LIMIT);
        assertThat(userRes.pagination().page()).isEqualTo(params.getPage() + 1);

        verify(userRepo, times(1)).findAllWithParams(params, minAgeDateBound, maxAgeDateBound, offset);
        verify(userRepo, never()).countTotal(params, minAgeDateBound, maxAgeDateBound);
    }

    @Test
    @DisplayName("Проверка нахождения списка пользователей по совпадению города")
    public void shouldReturnUsersByCityCoincidence() {
        SearchParams params = new SearchParams();
        params.setCity("Novosibirsk");
        LocalDate minAgeDateBound = LocalDate.now().minusYears(params.getMaxAge());
        LocalDate maxAgeDateBound = LocalDate.now().minusYears(params.getMinAge());
        int offset = params.getPage() * LIMIT;

        when(userRepo.findAllWithParams(params, minAgeDateBound, maxAgeDateBound, offset)).thenReturn(List.of(users.getLast()));
        when(userRepo.countTotal(params, minAgeDateBound, maxAgeDateBound)).thenReturn(1L);
        Users userRes = userService.findAll(params);

        assertThat(userRes).isNotNull();
        assertThat(userRes.users()).isNotNull();
        assertThat(userRes.users().size()).isEqualTo(1);
        assertThat(userRes.pagination()).isNotNull();
        assertThat(userRes.pagination().total()).isEqualTo(1);
        assertThat(userRes.pagination().size()).isEqualTo(LIMIT);
        assertThat(userRes.pagination().page()).isEqualTo(params.getPage() + 1);

        verify(userRepo, times(1)).findAllWithParams(params, minAgeDateBound, maxAgeDateBound, offset);
        verify(userRepo, times(1)).countTotal(params, minAgeDateBound, maxAgeDateBound);
    }

    @Test
    @DisplayName("Проверка на пустой список при не совпадении города")
    public void shouldReturnEmptyUsersWithNoCityCoincidence() {
        SearchParams params = new SearchParams();
        params.setSearchName("Moscow");
        LocalDate minAgeDateBound = LocalDate.now().minusYears(params.getMaxAge());
        LocalDate maxAgeDateBound = LocalDate.now().minusYears(params.getMinAge());
        int offset = params.getPage() * LIMIT;

        when(userRepo.findAllWithParams(params, minAgeDateBound, maxAgeDateBound, offset)).thenReturn(List.of());
        Users userRes = userService.findAll(params);

        assertThat(userRes).isNotNull();
        assertThat(userRes.users()).isNotNull();
        assertThat(userRes.users().size()).isEqualTo(0);
        assertThat(userRes.pagination()).isNotNull();
        assertThat(userRes.pagination().total()).isEqualTo(0);
        assertThat(userRes.pagination().size()).isEqualTo(LIMIT);
        assertThat(userRes.pagination().page()).isEqualTo(params.getPage() + 1);

        verify(userRepo, times(1)).findAllWithParams(params, minAgeDateBound, maxAgeDateBound, offset);
        verify(userRepo, never()).countTotal(params, minAgeDateBound, maxAgeDateBound);
    }
}
