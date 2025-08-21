package com.backend.dogwalks.user.service;

import com.backend.dogwalks.exception.custom_exception.EntityAlreadyExistsException;
import com.backend.dogwalks.exception.custom_exception.EntityNotFoundException;
import com.backend.dogwalks.user.dto.user.CustomUserResponse;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName(("CustomUserService Unit Tests"))
public class CustomUserServiceTest {

    @Mock
    private CustomUserRepository customUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomUserService customUserService;

    private CustomUser testUser;
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "Maria";
    private static final String EMAIL = "maria@test.com";
    private static final String PASSWORD = "encodedPassword";
    private static final String IMG_URL = "img.png";

    @BeforeEach
    void setUp() {
        testUser = new CustomUser();
        testUser.setId(USER_ID);
        testUser.setUsername(USERNAME);
        testUser.setEmail(EMAIL);
        testUser.setPassword(PASSWORD);
        testUser.setUserImgUrl(IMG_URL);
        testUser.setRole(Role.USER);
        testUser.setIsActive(true);
    }

    @Nested
    @DisplayName("Get My Profile Tests")
    class GetMyProfileTests {

        @Test
        @DisplayName("Should return user profile when user exist and is active")
        void shouldReturnUserProfile_whenUserExistAndIsActive() {

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));

            CustomUserResponse result = customUserService.getMyProfile(USER_ID);

            assertNotNull(result);
            assertEquals(USER_ID, result.id());
            assertEquals(USERNAME, result.username());
            assertEquals(EMAIL, result.email());
            assertEquals(IMG_URL, result.userImgUrl());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user not found")
        void shouldThrowEntityNotFoundException_whenUserNotFound() {

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> customUserService.getMyProfile(USER_ID));

            assertEquals("Active user with id: " + USER_ID + " not found", exception.getMessage());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user is inactive")
        void ShouldThrowEntityNotFoundException_whenUserIsInactive() {

            testUser.setIsActive(false);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> customUserService.getMyProfile(USER_ID));

            assertEquals("Active user with id: " + USER_ID + " not found", exception.getMessage());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
        }
    }

    @Nested
    @DisplayName("Update My Profile Tests")
    class UpdateMyProfileTests {

        @Test
        @DisplayName("Should update username when provided and different")
        void shouldUpdateUsername_whenProvidedAndDifferent() {

            String newUsername = "Pepa";

        }
    }




}
