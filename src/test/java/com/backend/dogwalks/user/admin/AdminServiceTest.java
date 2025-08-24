package com.backend.dogwalks.user.admin;

import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import com.backend.dogwalks.user.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Service Unit Tests")
public class AdminServiceTest {

    @Mock
    private CustomUserRepository customUserRepository;

    @InjectMocks
    private AdminService adminService;

    private CustomUser testUser;
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "Maria";
    private static final String EMAIL = "maria@test.com";
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
    }

    @Nested
    @DisplayName("Get Users Tests")
    class GetUsersTests {

    }
}
