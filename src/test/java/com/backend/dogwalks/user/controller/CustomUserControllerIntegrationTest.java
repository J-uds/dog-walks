package com.backend.dogwalks.user.controller;

import com.backend.dogwalks.security.user.CustomUserDetails;
import com.backend.dogwalks.security.user.jwt.JwtUtil;
import com.backend.dogwalks.user.dto.user.CustomUserUpdateEmailRequest;
import com.backend.dogwalks.user.dto.user.CustomUserUpdatePasswordRequest;
import com.backend.dogwalks.user.dto.user.CustomUserUpdateRequest;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import com.backend.dogwalks.utils.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
@Import(IntegrationTestUtils.class)
public class CustomUserControllerIntegrationTest {

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
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IntegrationTestUtils integrationTestUtils;

    private CustomUser user;
    private CustomUserDetails userDetails;
    private String authToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = integrationTestUtils.createUser(
                "Maria",
                "maria@test.com",
                "Testpassword547,",
                Role.USER
        );

        authToken = integrationTestUtils.generateToken(user);
    }

    @Test
    void getMyProfile_shouldReturnsUser_whenAuthenticated () throws Exception {

        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@test.com"));
    }

    @Test
    void getMyProfile_shouldReturnUnauthorized_WhenNoToken() throws Exception {

        mockMvc.perform(get("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateMyProfile_shouldUpdateUser_whenValidRequest() throws Exception {

        CustomUserUpdateRequest request = new CustomUserUpdateRequest("Pepa", null);

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(integrationTestUtils.toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Pepa"))
                .andExpect(jsonPath("$.email").value("maria@test.com"));

        CustomUser updatedUser = userRepository.findById(user.getId()).get();
        assertEquals("Pepa", updatedUser.getUsername());
    }

    @Test
    void updateMyEmail_shouldUpdateEmail_whenValidRequest() throws Exception {

        CustomUserUpdateEmailRequest request = new CustomUserUpdateEmailRequest("new@test.com", "Testpassword547,");

        mockMvc.perform(put("/api/users/profile/email")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(integrationTestUtils.toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"));

        CustomUser updatedUser = userRepository.findById(user.getId()).get();
        assertEquals("new@test.com", updatedUser.getEmail());
    }

    @Test
    void updateMyPassword_shouldUpdatePassword_whenValidRequest() throws Exception {

        CustomUserUpdatePasswordRequest request = new CustomUserUpdatePasswordRequest( "Testpassword547,", "NewPassword874,", "NewPassword874,");

        mockMvc.perform(put("/api/users/profile/password")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(integrationTestUtils.toJson(request)))
                .andExpect(status().isOk());

        CustomUser updatedUser = userRepository.findById(user.getId()).get();
        assertTrue(passwordEncoder.matches("NewPassword874,", updatedUser.getPassword()));
    }

    @Test
    void deactivateMyProfile_shouldDeactivateUser_whenUserIsAuthenticated() throws Exception {

        mockMvc.perform(delete("/api/users/profile/deactivate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        CustomUser deactivatedUser = userRepository.findById(user.getId()).get();
        assertFalse(deactivatedUser.getIsActive());
    }
}


