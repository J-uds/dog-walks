package com.backend.dogwalks.security;

import com.backend.dogwalks.security.user.CustomUserDetails;
import com.backend.dogwalks.security.user.jwt.JwtUtil;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtil Unit Tests")
public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;


    private CustomUserDetails userDetails;
    private static final Long USER_ID = 1L;
    private static final String EMAIL = "maria@test.com";
    private static final String SECRET = "mariapupurrupupu24987oiurhbsj54aporppwirnhfns4704nmvh479235";
    private static final Long EXPIRATION = 3600000L;

    @BeforeEach
    void setUp() {
        jwtUtil.setJwtSecretKey(SECRET);
        jwtUtil.setJwtExpiration(EXPIRATION);

        CustomUser user = new CustomUser();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setPassword("Password852,");
        user.setRole(Role.USER);
        user.setIsActive(true);

        userDetails = new CustomUserDetails(user);
    }

    @Nested
    @DisplayName("Generate Token Tests")
    class GenerateTokenTests {

        @Test
        @DisplayName("generateToken should return a valid JWT string")
        void generateTokenShouldReturnValidToken() {

            String token = jwtUtil.generateToken(userDetails);

            assertNotNull(token);
            assertEquals(3, token.split("\\.").length);
        }
    }

    @Nested
    @DisplayName("Extract Claims Tests")
    class ExtractClaimsTests {

        private String token;

        @BeforeEach
        void generateToken() {

            token = jwtUtil.generateToken(userDetails);
        }

        @Test
        @DisplayName("extractEmail should return the correct e-mail from token")
        void extractEmailShouldReturnCorrectEmail() {

            String email = jwtUtil.extractEmail(token);

            assertEquals(EMAIL, email);
        }

        @Test
        @DisplayName("extractId should return the correct id from token")
        void extractIdShouldReturnCorrectId() {

            Long id = jwtUtil.extractId(token);

            assertEquals(USER_ID, id);
        }
    }

    @Nested
    @DisplayName("Validate Token Tests")
    class ValidatedTokenTest {

        private String token;

        @BeforeEach
        void generateToken() {

            token = jwtUtil.generateToken(userDetails);
        }

        @Test
        @DisplayName("isValidToken should return true for a valid token")
        void isValidTokenShouldReturnTrueForValidToken() {

            assertTrue(jwtUtil.isValidToken(token));
        }

        @Test
        @DisplayName("isValidToken should return false for an invalid token")
        void isValidTokenShouldReturnFalseForInvalidToken() {

            String invalidToken = token + "blabla";

            assertFalse(jwtUtil.isValidToken(invalidToken));
        }
    }
 }
