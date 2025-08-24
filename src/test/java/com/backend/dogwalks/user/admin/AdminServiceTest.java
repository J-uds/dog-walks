package com.backend.dogwalks.user.admin;

import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import com.backend.dogwalks.user.service.AdminService;
import com.backend.dogwalks.walk.entity.Walk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Service Unit Tests")
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
        testWalk.setDateTime(2025-08-24 09:30:00);
        testWalk.setLocation("Narnia");
        testWalk.setDuration(60);
        testWalk.setDescription("bal bla bla");
        testWalk.setWalkImgUrl("testWalk.png");
        testWalk.setIsActive(true);
        testWalk.setUser(testUser);
    }

    @Nested
    @DisplayName("Get Users Tests")
    class GetUsersTests {

    }
}
