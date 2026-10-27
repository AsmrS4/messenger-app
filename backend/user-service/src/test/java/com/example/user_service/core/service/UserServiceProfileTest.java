package com.example.user_service.core.service;

import com.example.user_service.core.models.UserProfile;
import com.example.user_service.core.persistence.UserRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceProfileTest {
    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    private static UserProfile userProfile;
    private static UUID userId = UUID.randomUUID();

    @BeforeAll
    static void setUp() {
        userProfile = UserProfile.builder()
                .id(userId)
                .fullName("User1")
                .login("test_user1")
                .email("user1@example.com")
                .phone("+79005553535")
                .city("Tomsk")
                .birthDate(LocalDate.parse("2000-01-01"))
                .build();
    }

    @Test
    @DisplayName("Проверка на получение существующего пользователя по uuid")
    public void shouldFindUserByUUID() {
        when(userRepo.findById(userId)).thenReturn(userProfile);

        UserProfile found = userService.getProfileByUUID(userId);

        assertThat(found).isNotNull();
        assertEquals(userId, found.id());

        verify(userRepo, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Проверка на выброс исключения при попытке получить пользователя по несуществующему uuid")
    public void shouldThrowNoSuchElementExceptionWithNotFoundUserByUUID() {
        when(userRepo.findById(userId)).thenReturn(null);

        Exception ex = assertThrows(NoSuchElementException.class, () -> {
            userService.getProfileByUUID(userId);
        });

        assertEquals("Пользователь не найден", ex.getMessage());
        verify(userRepo, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Проверка на получение существующего пользователя по логину")
    public void shouldFindUserByLogin() {
        String login = anyString();

        when(userRepo.existsByLogin(login)).thenReturn(true);
        when(userRepo.findByLogin(login)).thenReturn(userProfile);

        UserProfile found = userService.getProfileByLogin(login);

        assertThat(found).isNotNull();
        assertEquals(userId, found.id());

        verify(userRepo, times(1)).findByLogin(login);
    }

    @Test
    @DisplayName("Проверка на выброс исключения при попытке получить пользователя по несуществующему логину")
    public void shouldThrowNoSuchElementExceptionWithNotFoundUserByLogin() {
        String login = anyString();
        when(userRepo.existsByLogin(login)).thenReturn(false);

        Exception ex = assertThrows(NoSuchElementException.class, () -> {
            userService.getProfileByLogin(login);
        });

        assertEquals("Пользователь не найден", ex.getMessage());
        verify(userRepo, times(1)).existsByLogin(login);
        verify(userRepo, never()).findByLogin(login);
    }
}
