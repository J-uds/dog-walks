package com.backend.dogwalks.user.service;

import com.backend.dogwalks.exception.custom_exception.EntityAlreadyExistsException;
import com.backend.dogwalks.exception.custom_exception.EntityNotFoundException;
import com.backend.dogwalks.user.dto.admin.AdminUserRequest;
import com.backend.dogwalks.user.dto.admin.AdminUserResponse;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import com.backend.dogwalks.walk.entity.Walk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService Unit Tests")
public class AdminServiceTest {

    @Mock
    private CustomUserRepository customUserRepository;

    @InjectMocks
    private AdminService adminService;

    private CustomUser testUser;
    private CustomUser testAdmin;
    private Walk testWalk;
    private static final Long USER_ID = 1L;
    private static final Long ADMIN_ID = 2L;
    private static final String USERNAME = "Maria";
    private static final String ADMIN_USERNAME = "Admin";
    private static final String EMAIL = "maria@test.com";
    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String IMG_URL = "img.png";

    @BeforeEach
    void setUp() {
        testUser = new CustomUser();
        testUser.setId(USER_ID);
        testUser.setUsername(USERNAME);
        testUser.setEmail(EMAIL);
        testUser.setUserImgUrl(IMG_URL);
        testUser.setRole(Role.USER);
        testUser.setIsActive(true);
        testUser.setWalks(new ArrayList<>());

        testAdmin = new CustomUser();
        testAdmin.setId(ADMIN_ID);
        testAdmin.setUsername(ADMIN_USERNAME);
        testAdmin.setEmail(ADMIN_EMAIL);
        testAdmin.setUserImgUrl("admin.png");
        testAdmin.setRole(Role.ADMIN);
        testAdmin.setIsActive(true);
        testAdmin.setWalks(new ArrayList<>());

        testWalk = new Walk();
        testWalk.setId(1L);
        testWalk.setTitle("test walk");
        testWalk.setLocation("Narnia");
        testWalk.setDuration(60);
        testWalk.setDescription("bal bla bla");
        testWalk.setWalkImgUrl("testWalk.png");
        testWalk.setIsActive(true);
        testWalk.setUser(testUser);

        testUser.getWalks().add(testWalk);
    }

    @Nested
    @DisplayName("Get Users Tests")
    class GetUsersTests {

        @Test
        @DisplayName("GetAllUsersPaginated should return paginated users with walks")
        void getAllUsersPaginatedShouldReturnPaginatedUsersWithWalks() {

            int page = 0;
            int size = 10;
            String sortBy = "id";
            String sortDirection = "ASC";

            List<CustomUser> users = List.of(testUser, testAdmin);
            Page<CustomUser> userPage = new PageImpl<>(users, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy)), users.size());

            when(customUserRepository.findAll(any(Pageable.class))).thenReturn(userPage);

            Page<AdminUserResponse> result = adminService.getAllUsersPaginated(page, size, sortBy, sortDirection);

            assertNotNull(result);
            assertEquals(2, result.getContent().size());

            AdminUserResponse testUserResponse = result.getContent().getFirst();
            assertEquals(USER_ID, testUserResponse.id());
            assertEquals(USERNAME, testUserResponse.username());
            assertEquals(EMAIL, testUserResponse.email());
            assertEquals(IMG_URL, testUserResponse.userImgUrl());
            assertEquals(Role.USER, testUserResponse.role());
            assertTrue(testUserResponse.isActive());
            assertEquals(1, testUserResponse.walks().size());

            AdminUserResponse testAdminResponse = result.getContent().get(1);
            assertEquals(ADMIN_ID, testAdminResponse.id());
            assertEquals(ADMIN_USERNAME, testAdminResponse.username());
            assertEquals(ADMIN_EMAIL, testAdminResponse.email());
            assertEquals("admin.png", testAdminResponse.userImgUrl());
            assertEquals(Role.ADMIN, testAdminResponse.role());
            assertTrue(testAdminResponse.isActive());
            assertEquals(0, testAdminResponse.walks().size());

            verify(customUserRepository, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("GetUserById should return user by id")
        void getUserByIdShouldReturnUserById() {

            when(customUserRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            AdminUserResponse result = adminService.getUserById(USER_ID);

            assertNotNull(result);
            assertEquals(USER_ID, result.id());
            assertEquals(USERNAME, result.username());
            assertEquals(EMAIL, result.email());
            assertEquals(IMG_URL, result.userImgUrl());
            assertEquals(Role.USER, result.role());
            assertTrue(result.isActive());

            assertNotNull(result.walks());
            assertEquals(1, result.walks().size());
            assertEquals(1L, result.walks().getFirst().id());
            assertEquals("test walk", result.walks().getFirst().title());
            assertEquals("Narnia", result.walks().getFirst().location());

            verify(customUserRepository, times(1)).findById(USER_ID);
        }

        @Test
        @DisplayName("GetUserById should throw EntityNotFoundException when user not found")
        void getUserByIdShouldThrowEntityNotFoundException_whenUserNotFound() {

            when(customUserRepository.findById(USER_ID)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> adminService.getUserById(USER_ID));

            assertEquals("User with id: " + USER_ID + " not found", exception.getMessage());

            verify(customUserRepository, times(1)).findById(USER_ID);
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("UpdateUser should update user successfully")
        void updateUserShouldUpdateUserSuccessfully() {

            AdminUserRequest request = new AdminUserRequest(
                    "updatedUser",
                    "update@test.com",
                    "UpdateImg.png",
                    Role.USER,
                    true);

            when(customUserRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(customUserRepository.existsByEmailAndIdNot(request.email(), USER_ID)).thenReturn(false);
            when(customUserRepository.save(any(CustomUser.class))).thenReturn(testUser);

            AdminUserResponse result = adminService.updateUser(USER_ID, request);

            assertNotNull(result);
            assertEquals("updatedUser", result.username());
            assertEquals("update@test.com", result.email());
            assertEquals("UpdateImg.png", result.userImgUrl());
            assertEquals(Role.USER, result.role());
            assertTrue(result.isActive());

            verify(customUserRepository, times(1)).findById(USER_ID);
            verify(customUserRepository, times(1)).existsByEmailAndIdNot(request.email(), USER_ID);
            verify(customUserRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("UpdateUser should throw EntityNotFoundException when user not found")
        void updateUserShouldThrowException_whenUserNotFound() {

            AdminUserRequest request = new AdminUserRequest(
                    "updatedUser",
                    "update@test.com",
                    "UpdateImg.png",
                    Role.USER,
                    true);

            when(customUserRepository.findById(USER_ID)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> adminService.getUserById(USER_ID));

            assertEquals("User with id: " + USER_ID + " not found", exception.getMessage());

            verify(customUserRepository, times(1)).findById(USER_ID);
            verify(customUserRepository, never()).save(any());
        }

        @Test
        @DisplayName("UpdateUser should throw EntityAlreadyExistException when email already exist")
        void updateUserShouldThrowException_whenEmailAlreadyExist() {

            AdminUserRequest request = new AdminUserRequest(
                    "updatedUser",
                    "update@test.com",
                    "UpdateImg.png",
                    Role.USER,
                    true);

            when(customUserRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(customUserRepository.existsByEmailAndIdNot(request.email(), USER_ID)).thenReturn(true);

            EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () -> adminService.updateUser(USER_ID, request));

            assertEquals("E-mail " + request.email() + " is already registered", exception.getMessage());

            verify(customUserRepository, times(1)).findById(USER_ID);
            verify(customUserRepository, never()).save(any());
        }

        @Test
        @DisplayName("UpdateUser should throw IllegalStateException when trying to change role of last admin")
        void updateUserShouldThrowException_whenTryingToChangeRoleOfLastAdmin() {

            AdminUserRequest request = new AdminUserRequest(
                    "updatedUser",
                    "update@test.com",
                    "UpdateImg.png",
                    Role.USER,
                    true);

            when(customUserRepository.findById(ADMIN_ID)).thenReturn(Optional.of(testAdmin));
            when(customUserRepository.existsByEmailAndIdNot(request.email(), ADMIN_ID)).thenReturn(false);
            when(customUserRepository.countByRoleAndIsActive(Role.ADMIN, true)).thenReturn(1L);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> adminService.updateUser(ADMIN_ID, request));

            assertEquals("Cannot change the role of the last active admin", exception.getMessage());

            verify(customUserRepository, times(1)).findById(ADMIN_ID);
            verify(customUserRepository, never()).save(any());
        }

        @Test
        @DisplayName("UpdateUser should throw IllegalStateException when trying to deactivate last admin")
        void updateUserShouldThrowException_whenTryingToDeactivateLastAdmin() {

            AdminUserRequest request = new AdminUserRequest(
                    "updatedUser",
                    "update@test.com",
                    "UpdateImg.png",
                    Role.ADMIN,
                    false);

            when(customUserRepository.findById(ADMIN_ID)).thenReturn(Optional.of(testAdmin));
            when(customUserRepository.existsByEmailAndIdNot(request.email(), ADMIN_ID)).thenReturn(false);
            when(customUserRepository.countByRoleAndIsActive(Role.ADMIN, true)).thenReturn(1L);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> adminService.updateUser(ADMIN_ID, request));

            assertEquals("Cannot deactivate the last active admin", exception.getMessage());

            verify(customUserRepository, times(1)).findById(ADMIN_ID);
            verify(customUserRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("DeleteUser should delete user successfully")
        void deleteUserShouldDeleteUserSuccessfully() {

            when(customUserRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            adminService.deleteUser(USER_ID);

            verify(customUserRepository, times(1)).findById(USER_ID);
            verify(customUserRepository, times(1)).delete(testUser);
        }

        @Test
        @DisplayName("DeleteUser should throw EntityNotFoundException when user not found")
        void deleteUserShouldThrowException_whenUserNotFound() {

            when(customUserRepository.findById(USER_ID)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> adminService.deleteUser(USER_ID));

            assertEquals("User with id: " + USER_ID + " not found", exception.getMessage());

            verify(customUserRepository, times(1)).findById(USER_ID);
            verify(customUserRepository, never()).delete(any());
        }

        @Test
        @DisplayName("DeleteUser should throw IllegalStateException when trying to delete last admin")
        void deleteUserShouldThrowException_whenTryingToDeactivateLastAdmin() {

            when(customUserRepository.findById(ADMIN_ID)).thenReturn(Optional.of(testAdmin));
            when(customUserRepository.countByRoleAndIsActive(Role.ADMIN, true)).thenReturn(1L);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> adminService.deleteUser(ADMIN_ID));

            assertEquals("Cannot delete the last active admin", exception.getMessage());

            verify(customUserRepository, times(1)).findById(ADMIN_ID);
            verify(customUserRepository, never()).delete(any());
        }
    }
}
