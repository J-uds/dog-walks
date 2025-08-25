package com.backend.dogwalks.user.admin;

import com.backend.dogwalks.user.dto.admin.AdminUserResponse;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import com.backend.dogwalks.user.service.AdminService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        @DisplayName("Should return paginated users with walks")
        void shouldReturnPaginatedUsersWithWalks() {

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

            AdminUserResponse adminResponse = result.getContent().get(0);
            assertEquals(USER_ID, adminResponse.id());
            assertEquals(USERNAME, adminResponse.username());
            assertEquals(EMAIL, adminResponse.email());
            assertEquals(IMG_URL, adminResponse.userImgUrl());
            assertEquals(Role.USER, adminResponse.userImgUrl());
            assertTrue(adminResponse.isActive());
            assertEquals(1, adminResponse.walks().size());
        }
    }
}
