package com.backend.dogwalks.user.controller;

import com.backend.dogwalks.security.user.CustomUserDetails;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import com.backend.dogwalks.utils.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
                .andExpect(jsonPath("$.Size", is(10)))
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
}
