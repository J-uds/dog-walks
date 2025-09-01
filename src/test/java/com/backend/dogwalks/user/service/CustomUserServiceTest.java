package com.backend.dogwalks.user.service;

import com.backend.dogwalks.exception.custom_exception.EntityAlreadyExistsException;
import com.backend.dogwalks.exception.custom_exception.EntityNotFoundException;
import com.backend.dogwalks.exception.custom_exception.InvalidCredentialsException;
import com.backend.dogwalks.user.dto.user.CustomUserResponse;
import com.backend.dogwalks.user.dto.user.CustomUserUpdateEmailRequest;
import com.backend.dogwalks.user.dto.user.CustomUserUpdatePasswordRequest;
import com.backend.dogwalks.user.dto.user.CustomUserUpdateRequest;
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

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName(("CustomUser Service Unit Tests"))
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
        testUser.setWalks(new ArrayList<>());
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
        void shouldThrowEntityNotFoundException_whenUserIsInactive() {

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
        @DisplayName("Should update username when provided")
        void shouldUpdateUsername_whenProvided() {

            String newUsername = "Pepa";
            CustomUserUpdateRequest request = new CustomUserUpdateRequest(newUsername, null);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));
            when(customUserRepository.save(any(CustomUser.class))).thenReturn(testUser);

            CustomUserResponse result = customUserService.updateMyProfile(USER_ID, request);

            assertEquals(newUsername, result.username());
            assertEquals(newUsername, testUser.getUsername());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
            verify(customUserRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("Should update image URL when provided")
        void shouldUpdateImageUrl_whenProvided() {

            String newImageUrl = "img2.png";
            CustomUserUpdateRequest request = new CustomUserUpdateRequest(null, newImageUrl);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));
            when(customUserRepository.save(any(CustomUser.class))).thenReturn(testUser);

            CustomUserResponse result = customUserService.updateMyProfile(USER_ID, request);

            assertEquals(newImageUrl, result.userImgUrl());
            assertEquals(newImageUrl, testUser.getUserImgUrl());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
            verify(customUserRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("Should not update when fields are null")
        void shouldNotUpdate_whenFieldsAreNull() {
            String originalUsername = testUser.getUsername();
            String originalUserImgUrl = testUser.getUserImgUrl();
            CustomUserUpdateRequest request = new CustomUserUpdateRequest(null, null);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));
            when(customUserRepository.save(any(CustomUser.class))).thenReturn(testUser);

            customUserService.updateMyProfile(USER_ID, request);

            assertEquals(originalUsername, testUser.getUsername());
            assertEquals(originalUserImgUrl, testUser.getUserImgUrl());

            verify(customUserRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user not found")
        void shouldThrowEntityNotFoundException_whenUserNotFound() {

            CustomUserUpdateRequest request = new CustomUserUpdateRequest("nweUsername", null);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> customUserService.updateMyProfile(USER_ID, request));

            assertEquals("Active user with id: " + USER_ID + " not found", exception.getMessage());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
            verify(customUserRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update My Email Tests")
    class UpdateMyEmailTests {

        @Test
        @DisplayName("Should update e-mail when all validations pass")
        void shouldUpdateEmail_whenAllValidationsPass() {

            String newEmail = "new@test.com";
            String rawPassword = "rawPassword9.";
            CustomUserUpdateEmailRequest request = new CustomUserUpdateEmailRequest(newEmail,rawPassword);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(rawPassword, PASSWORD)).thenReturn(true);
            when(customUserRepository.existsByEmail(newEmail)).thenReturn(false);
            when(customUserRepository.save(any(CustomUser.class))).thenReturn(testUser);

            CustomUserResponse result = customUserService.updateMyEmail(USER_ID, request);

            assertEquals(newEmail, result.email());
            assertEquals(newEmail, testUser.getEmail());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
            verify(passwordEncoder, times(1)).matches(rawPassword, PASSWORD);
            verify(customUserRepository, times(1)).existsByEmail(newEmail);
            verify(customUserRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when password is incorrect")
        void shouldThrowInvalidCredentialsException_whenPasswordIsIncorrect() {
            String newEmail = "new@test.com";
            String wrongPassword = "wrongPassword9.";
            CustomUserUpdateEmailRequest request = new CustomUserUpdateEmailRequest(newEmail,wrongPassword);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(wrongPassword, PASSWORD)).thenReturn(false);

            InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> customUserService.updateMyEmail(USER_ID, request));

            assertEquals("Invalid Password", exception.getMessage());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
            verify(passwordEncoder, times(1)).matches(wrongPassword, PASSWORD);
            verify(customUserRepository, never()).existsByEmail(anyString());
            verify(customUserRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw EntityAlreadyExistsException when e-mail already exist")
        void shouldThrowEntityAlreadyExistsException_whenEmailAlreadyExist() {
            String existingEmail = "existing@test.com";
            String rawPassword = "rawPassword9.";
            CustomUserUpdateEmailRequest request = new CustomUserUpdateEmailRequest(existingEmail,rawPassword);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(rawPassword, PASSWORD)).thenReturn(true);
            when(customUserRepository.existsByEmail(existingEmail)).thenReturn(true);

            EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () -> customUserService.updateMyEmail(USER_ID, request));

            assertEquals("E-mail " + existingEmail + " is already registered", exception.getMessage());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
            verify(passwordEncoder, times(1)).matches(rawPassword, PASSWORD);
            verify(customUserRepository, times(1)).existsByEmail(existingEmail);
            verify(customUserRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when new e-mail equals current e-mail")
        void shouldThrowInvalidCredentialsException_whenNewEmailEqualsCurrentEmail() {

            String rawPassword = "rawPassword9.";
            CustomUserUpdateEmailRequest request = new CustomUserUpdateEmailRequest(EMAIL, rawPassword);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(rawPassword, PASSWORD)).thenReturn(true);
            when(customUserRepository.existsByEmail(EMAIL)).thenReturn(false);

            InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> customUserService.updateMyEmail(USER_ID, request));

            assertEquals("New e-mail must be different from current e-mail", exception.getMessage());

            verify(customUserRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update My Password Tests")
    class UpdateMyPasswordTests {

        @Test
        @DisplayName("Should update password when all validations pass")
        void shouldUpdatePassword_whenAllValidationsPass() {

            String oldPassword = "oldPassword7.";
            String newPassword = "newPassword7.";
            String encodedPassword = "encodedNewPassword7.";
            CustomUserUpdatePasswordRequest request = new CustomUserUpdatePasswordRequest(oldPassword, newPassword, newPassword);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(oldPassword, PASSWORD)).thenReturn(true);
            when(passwordEncoder.matches(newPassword, PASSWORD)).thenReturn(false);
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

            customUserService.updateMyPassword(USER_ID, request);

            assertEquals(encodedPassword, testUser.getPassword());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
            verify(passwordEncoder, times(1)).matches(oldPassword, PASSWORD);
            verify(passwordEncoder, times(1)).encode(newPassword);
            verify(customUserRepository,times(1)).save(testUser);
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when old password is incorrect")
        void shouldThrowInvalidCredentialsException_whenOldPasswordIsIncorrect() {

            String wrongOldPassword = "wrongOldPassword7.";
            String newPassword = "newPassword7.";
            CustomUserUpdatePasswordRequest request = new CustomUserUpdatePasswordRequest(wrongOldPassword, newPassword, newPassword);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(wrongOldPassword, PASSWORD)).thenReturn(false);

            InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> customUserService.updateMyPassword(USER_ID, request));

            assertEquals("Current password is incorrect", exception.getMessage());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
            verify(passwordEncoder, times(1)).matches(wrongOldPassword, PASSWORD);
            verify(passwordEncoder, never()).encode(anyString());
            verify(customUserRepository,never()).save(any());
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when passwords do not match")
        void shouldThrowInvalidCredentialsException_whenPasswordsDoNotMatch() {

            String oldPassword = "oldPassword7.";
            String newPassword = "newPassword7.";
            String differentConfirmPassword = "differentConfirmPassword7.";
            CustomUserUpdatePasswordRequest request = new CustomUserUpdatePasswordRequest(oldPassword, newPassword, differentConfirmPassword);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(oldPassword, PASSWORD)).thenReturn(true);

            InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> customUserService.updateMyPassword(USER_ID, request));

            assertEquals("New password and confirmation do not match", exception.getMessage());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
            verify(passwordEncoder, times(1)).matches(oldPassword, PASSWORD);
            verify(passwordEncoder, never()).encode(anyString());
            verify(customUserRepository,never()).save(any());
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when new password equals current password")
        void shouldThrowInvalidCredentialsException_whenNewPasswordsEqualsCurrentPassword() {

            String oldPassword = "oldPassword7.";
            String samePassword = "oldPassword7.";
            CustomUserUpdatePasswordRequest request = new CustomUserUpdatePasswordRequest(oldPassword, samePassword, samePassword);

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(oldPassword, PASSWORD)).thenReturn(true);
            when(passwordEncoder.matches(samePassword, PASSWORD)).thenReturn(true);

            InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> customUserService.updateMyPassword(USER_ID, request));

            assertEquals("New password must be different from current password", exception.getMessage());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
            verify(passwordEncoder, times(2)).matches(oldPassword, PASSWORD);
            verify(passwordEncoder, never()).encode(anyString());
            verify(customUserRepository,never()).save(any());
        }
    }

    @Nested
    @DisplayName("Deactivate My Profile Tests")
    class DeactivateMyProfileTests {

        @Test
        @DisplayName("Should deactivate user profile when user exists and is active")
        void shouldDeactivateUserProfile_whenUserExistsAndIsActive() {

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.of(testUser));

            customUserService.deactivateMyProfile(USER_ID);

            assertFalse(testUser.getIsActive());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
            verify(customUserRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user not found")
        void shouldThrowEntityNotFoundException_whenUserNotFound() {

            when(customUserRepository.findByIdAndIsActive(USER_ID, true)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> customUserService.deactivateMyProfile(USER_ID));

            assertEquals("Active user with id: " + USER_ID + " not found", exception.getMessage());

            verify(customUserRepository, times(1)).findByIdAndIsActive(USER_ID, true);
            verify(customUserRepository, never()).save(any());
        }
    }

}
