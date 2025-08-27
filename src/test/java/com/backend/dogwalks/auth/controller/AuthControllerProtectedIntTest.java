package com.backend.dogwalks.auth.controller;

import com.backend.dogwalks.auth.dto.login.LoginRequest;
import com.backend.dogwalks.auth.dto.login.LoginResponse;
import com.backend.dogwalks.auth.dto.register.RegisterRequest;
import com.backend.dogwalks.auth.dto.register.RegisterResponse;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class AuthControllerProtectedIntTest {

    @Container
    static MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("dogwalks-test")
            .withUsername("test_user")
            .withPassword("test_password");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", () -> mySqlContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySqlContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySqlContainer.getPassword());
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private CustomUserRepository userRepository;

    private String baseUrl;

    private static final String USERNAME = "Maria";
    private static final String EMAIL = "maria@test.com";
    private static final String PASSWORD = "Test9876543.";

    @BeforeEach
    void setUp() {

        baseUrl = "http://localhost:" + port + "/api";
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Successful register")
    public void successfulRegister() {

        RegisterRequest registerRequest = new RegisterRequest(USERNAME,EMAIL,PASSWORD);

        ResponseEntity<RegisterResponse> registerResponse = restTemplate.postForEntity(baseUrl + "/register", registerRequest, RegisterResponse.class);

        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());
        assertEquals(EMAIL, registerResponse.getBody().email());
        assertEquals(USERNAME, registerResponse.getBody().username());

        CustomUser savedUser = userRepository.findUserByEmail(EMAIL).orElse(null);

        assertNotNull(savedUser);
        assertEquals(EMAIL, savedUser.getEmail());
        assertEquals(USERNAME, savedUser.getUsername());
        assertEquals(Role.USER, savedUser.getRole());
        assertTrue(savedUser.getIsActive());
    }

    @Test
    @DisplayName(("Successful login, 200 OK and JWT token"))
    public void successfulLogin() {

        RegisterRequest registerRequest = new RegisterRequest(USERNAME, EMAIL, PASSWORD);

        restTemplate.postForEntity(baseUrl + "/register", registerRequest, RegisterResponse.class);

        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(baseUrl + "/login", loginRequest, LoginResponse.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertEquals(EMAIL, loginResponse.getBody().email());
        assertEquals(USERNAME, loginResponse.getBody().username());
        assertEquals("Bearer", loginResponse.getBody().tokenType());
        assertNotNull(loginResponse.getBody().token());
        assertTrue(loginResponse.getBody().token().split("\\.").length == 3);
    }
}
