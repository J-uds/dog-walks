package com.backend.dogwalks.user.controller;

import com.backend.dogwalks.security.user.CustomUserDetails;
import com.backend.dogwalks.user.dto.admin.AdminUserRequest;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import com.backend.dogwalks.utils.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
@Import(IntegrationTestUtils.class)
@DisplayName("Admin Controller Integration Tests")
public class AdminControllerIntegrationTest {

    @Container
    static MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("dogwalks-test")
            .withUsername("test_user")
            .withPassword("test_password")
            .withReuse(false); //por defecto es así

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", () -> mySqlContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySqlContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySqlContainer.getPassword());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomUserRepository userRepository;

    @Autowired
    private IntegrationTestUtils integrationTestUtils;

    private CustomUser user;
    private CustomUser admin;
    private CustomUserDetails userDetails;
    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = integrationTestUtils.createUser(
                "Maria",
                "maria@test.com",
                "Testpassword547.",
                Role.USER
        );

        admin = integrationTestUtils.createUser(
                "Admin",
                "admin@test.com",
                "Testpassword547.",
                Role.ADMIN
        );

        userToken = integrationTestUtils.generateToken(user);
        adminToken = integrationTestUtils.generateToken(admin);
    }

    @Nested
    @DisplayName("Get Users")
    class GetUsers {

        @Test
        @DisplayName("GET /api/admin/users - must return paginated users when valid request")
        void getAllUsersPaginated_shouldReturnPaginatedUsers_whenValidRequest() throws Exception{

            mockMvc.perform(get("/api/admin/users")
                    .header("Authorization", "Bearer " + adminToken)
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortBy", "id")
                    .param("sortDirection", "ASC")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.size", is(10)))
                    .andExpect(jsonPath("$.number", is(0)));
        }

        @Test
        @DisplayName("GET /api/admin/users - must return 400 bad request when size is zero or negative")
        void getAllUsersPaginated_shouldReturnBadRequest_whenRequestSizeIsZero() throws Exception{

            mockMvc.perform(get("/api/admin/users")
                            .header("Authorization", "Bearer " + adminToken)
                            .param("page", "0")
                            .param("size", "0")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET /api/admin/users - must use safe defaults when invalid sort parameters")
        void getAllUsersPaginated_shouldReturnSafeDefaults_whenInvalidSorts() throws Exception{

            mockMvc.perform(get("/api/admin/users")
                            .header("Authorization", "Bearer " + adminToken)
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "invalid")
                            .param("sortDirection", "INVALID")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.size", is(10)))
                    .andExpect(jsonPath("$.number", is(0)));
        }

        @Test
        @DisplayName("GET /api/admin/users - must limit max size when large size is requested")
        void getAllUsersPaginated_shouldLimitMazSize_whenLargeSizeRequested() throws Exception{

            mockMvc.perform(get("/api/admin/users")
                            .header("Authorization", "Bearer " + adminToken)
                            .param("page", "0")
                            .param("size", "200")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size", lessThanOrEqualTo(100)));
        }

        @Test
        @DisplayName("GET /api/admin/users/{id} - must return user when valid id")
        void getUserById_shouldReturnUser_whenValidId() throws Exception{

            mockMvc.perform(get("/api/admin/users/{id}", user.getId())
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                    .andExpect(jsonPath("$.username", is("Maria")))
                    .andExpect(jsonPath("$.email", is("maria@test.com")))
                    .andExpect(jsonPath("$.role", is("USER")))
                    .andExpect(jsonPath("$.isActive", is(true)));
        }

        @Test
        @DisplayName("GET /api/admin/users/{id} - must return not found when invalid id")
        void getUserById_shouldReturnNotFound_whenInvalidId() throws Exception{

            mockMvc.perform(get("/api/admin/users/{id}", 99L)
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/admin/users - must return 401 unauthorized when regular user")
        void getUserById_shouldReturnUnauthorized_whenRegularUser() throws Exception{

            mockMvc.perform(get("/api/admin/users")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/admin/users - must return 401 unauthorized when no token")
        void getUserById_shouldReturnUnauthorized_whenNoToken() throws Exception{

            mockMvc.perform(get("/api/admin/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Update Users")
    class UpdateUsers {

        @Test
        @DisplayName("PUT /api/admin/users/{id} - must update user when valid request")
        void updateUser_shouldUpdateUser_whenValidRequest() throws Exception{

            AdminUserRequest request = new AdminUserRequest(
                    "Pepa",
                    "pepa@test.com",
                    "img.png",
                    Role.USER,
                    true
            );

            mockMvc.perform(put("/api/admin/users/{id}", user.getId())
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(integrationTestUtils.toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", is("Pepa")))
                    .andExpect(jsonPath("$.email", is("pepa@test.com")))
                    .andExpect(jsonPath("$.role", is("USER")))
                    .andExpect(jsonPath("$.isActive", is(true)));

            CustomUser updatedUser = userRepository.findById(user.getId()).get();
            assertEquals("Pepa", updatedUser.getUsername());
            assertEquals("pepa@test.com", updatedUser.getEmail());
        }

        @Test
        @DisplayName("PUT /api/admin/users/{id} - must return 409 conflict when emil already exist")
        void updateUser_shouldReturnConflict_whenEmailExist() throws Exception{

            AdminUserRequest request = new AdminUserRequest(
                    "Pepa",
                    "admin@test.com",
                    "img.png",
                    Role.USER,
                    true
            );

            mockMvc.perform(put("/api/admin/users/{id}", user.getId())
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(integrationTestUtils.toJson(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("Delete Users")
    class DeleteUsers {

        @Test
        @DisplayName("DELETE /api/admin/users/{id} - must delete user when valid request")
        void deleteUser_shouldUpdateUser_whenValidRequest() throws Exception{

            mockMvc.perform(delete("/api/admin/users/{id}", user.getId())
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            mockMvc.perform(delete("/api/admin/users/{id}", user.getId())
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("DELETE /api/admin/users/{id} - must return 404 not found when invalid id")
        void deleteUser_shouldReturnNotFound_whenInvalidId() throws Exception{

            mockMvc.perform(delete("/api/admin/users/{id}", 99L)
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }
}
