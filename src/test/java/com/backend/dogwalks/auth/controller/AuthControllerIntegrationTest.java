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
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@DisplayName("Auth Controller Integration Tests")
public class AuthControllerIntegrationTest {

    @Container
    static MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("dogwalks-test")
            .withUsername("test_user")
            .withPassword("test_password")
            .withReuse(false);

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

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    @DisplayName("POST /api/register - must register user successfully")
    void successfulRegister() {

        RegisterRequest request = new RegisterRequest(USERNAME,EMAIL,PASSWORD);
        HttpHeaders headers = createJsonHeaders();
        HttpEntity<RegisterRequest> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<RegisterResponse> response = restTemplate.exchange(baseUrl + "/register", HttpMethod.POST, requestEntity, RegisterResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(EMAIL, response.getBody().email());
        assertEquals(USERNAME, response.getBody().username());

        CustomUser savedUser = userRepository.findUserByEmail(EMAIL).orElse(null);

        assertNotNull(savedUser);
        assertEquals(EMAIL, savedUser.getEmail());
        assertEquals(USERNAME, savedUser.getUsername());
        assertEquals(Role.USER, savedUser.getRole());
        assertTrue(savedUser.getIsActive());

        assertNotEquals(PASSWORD, savedUser.getPassword());
        assertTrue(savedUser.getPassword().startsWith("$2a$"));
        assertTrue(passwordEncoder.matches(PASSWORD, savedUser.getPassword()));
    }

    @Test
    @DisplayName("POST /api/register - should reject duplicated email")
    void registerShouldRejectDuplicatedEmail() {
        RegisterRequest firstRequest = new RegisterRequest(USERNAME,EMAIL,PASSWORD);
        HttpEntity<RegisterRequest> firstEntity = new HttpEntity<>(firstRequest, createJsonHeaders());
        restTemplate.exchange(baseUrl + "/register", HttpMethod.POST, firstEntity, RegisterResponse.class);

        RegisterRequest secondRequest = new RegisterRequest("Ana",EMAIL,"2Test9876543.");
        HttpEntity<RegisterRequest> secondEntity = new HttpEntity<>(secondRequest, createJsonHeaders());
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/register", HttpMethod.POST, secondEntity, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("is already registered"));
        assertEquals(1, userRepository.count());
    }

    @Test
    @DisplayName("POST /api/register - must validate requested data")
    void registerMustValidateEntryData() {

        RegisterRequest request = new RegisterRequest("", "email", "123");
        HttpEntity<RegisterRequest> requestEntity = new HttpEntity<>(request, createJsonHeaders());
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/register", HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, userRepository.count());
    }

    @Test
    @DisplayName(("POST /api/login - must login user successfully and generate JWT token"))
    void successfulLogin() {

        registerUserHelper(USERNAME, EMAIL, PASSWORD);

        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        HttpEntity<LoginRequest> requestEntity =new HttpEntity<>(loginRequest, createJsonHeaders());
        ResponseEntity<LoginResponse> loginResponse = restTemplate.exchange(baseUrl + "/login", HttpMethod.POST, requestEntity, LoginResponse.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertEquals(EMAIL, loginResponse.getBody().email());
        assertEquals(USERNAME, loginResponse.getBody().username());
        assertEquals("Bearer", loginResponse.getBody().tokenType());
        assertNotNull(loginResponse.getBody().token());
        assertFalse(loginResponse.getBody().token().isEmpty());
        assertTrue(loginResponse.getBody().token().split("\\.").length == 3);
    }

    @Test
    @DisplayName("POST /api/login - should reject wrong credentials")
    void loginShouldRejectWrongCredentials() {

        registerUserHelper(USERNAME, EMAIL, PASSWORD);

        LoginRequest loginRequest = new LoginRequest(EMAIL, "123");
        HttpEntity<LoginRequest> requestEntity =new HttpEntity<>(loginRequest, createJsonHeaders());
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/login", HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Incorrect e-mail or password"));
    }

    @Test
    @DisplayName("POST /api/login - should reject non existing user")
    void loginShouldRejectNonExistingUser() {

        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        HttpEntity<LoginRequest> requestEntity =new HttpEntity<>(loginRequest, createJsonHeaders());
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/login", HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("not found"));
    }


    @Test
    @DisplayName("POST /api/login - should reject deactivated user login")
    void loginShouldRejectDeactivatedUser() {

        registerUserHelper(USERNAME, EMAIL, PASSWORD);

        CustomUser user = userRepository.findUserByEmail(EMAIL).get();
        user.setIsActive(false);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        HttpEntity<LoginRequest> requestEntity =new HttpEntity<>(loginRequest, createJsonHeaders());
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/login", HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().contains("User account is deactivated"));
    }

    @Test
    @DisplayName("Complete path: Register, login, valid JWT")
    void completePath() {

        RegisterRequest registerRequest = new RegisterRequest(USERNAME, EMAIL, PASSWORD);
        HttpEntity<RegisterRequest> registerEntity = new HttpEntity<>(registerRequest, createJsonHeaders());
        ResponseEntity<RegisterResponse> registerResponse = restTemplate.exchange(baseUrl + "/register", HttpMethod.POST, registerEntity, RegisterResponse.class);

        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());

        CustomUser savedUser = userRepository.findUserByEmail(EMAIL).get();
        assertEquals(EMAIL, savedUser.getEmail());
        assertTrue(savedUser.getIsActive());

        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        HttpEntity<LoginRequest> loginEntity =new HttpEntity<>(loginRequest, createJsonHeaders());
        ResponseEntity<LoginResponse> loginResponse = restTemplate.exchange(baseUrl + "/login", HttpMethod.POST, loginEntity, LoginResponse.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        String token = loginResponse.getBody().token();
        assertEquals("Bearer", loginResponse.getBody().tokenType());
        assertNotNull(loginResponse.getBody().token());
        assertFalse(loginResponse.getBody().token().isEmpty());
        assertTrue(loginResponse.getBody().token().split("\\.").length == 3);
        assertEquals(EMAIL, loginResponse.getBody().email());
    }

    private HttpHeaders createJsonHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        return headers;
    }

    private void registerUserHelper(String username, String email, String password) {

        RegisterRequest registerRequest = new RegisterRequest(username, email, password);

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(registerRequest, createJsonHeaders());

        restTemplate.exchange(baseUrl + "/register", HttpMethod.POST, entity, RegisterResponse.class);
    }
}
