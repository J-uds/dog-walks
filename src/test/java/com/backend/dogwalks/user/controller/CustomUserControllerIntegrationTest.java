package com.backend.dogwalks.user.controller;

import com.backend.dogwalks.security.user.CustomUserDetails;
import com.backend.dogwalks.security.user.jwt.JwtUtil;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
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

    private CustomUser user;
    private CustomUserDetails userDetails;
    private String jwt;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

    }
    @Test
    void getMyProfile_returnsUserDetails_whenAuthenticated () {

    }
}


