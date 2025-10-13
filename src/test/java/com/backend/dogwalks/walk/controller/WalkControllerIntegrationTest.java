package com.backend.dogwalks.walk.controller;

import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import com.backend.dogwalks.utils.IntegrationTestUtils;
import com.backend.dogwalks.walk.dto.WalkRequest;
import com.backend.dogwalks.walk.entity.Walk;
import com.backend.dogwalks.walk.repository.WalkRepository;
import org.junit.jupiter.api.*;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
@Import(IntegrationTestUtils.class)
@DisplayName("Walk Controller Integration Tests")
public class WalkControllerIntegrationTest {

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
    private MockMvc mockMvc;

    @Autowired
    private CustomUserRepository userRepository;

    @Autowired
    private WalkRepository walkRepository;

    @Autowired
    private IntegrationTestUtils integrationTestUtils;

    private CustomUser user;
    private CustomUser admin;
    private Walk walk;
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

        walk = new Walk(
                "Englischer Garten",
                "Munich",
                120,
                "Marvellous walk",
                "eg.png",
                true,
                user
        );
        walk =walkRepository.save(walk);

        userToken = integrationTestUtils.generateToken(user);
        adminToken = integrationTestUtils.generateToken(admin);
    }

    @Nested
    @DisplayName("Get Walks (Public) Tests")
    class GetPublicWalksTests {

        @Test
        @DisplayName("GET api/walks/public - must return walk summaries when no authentication required")
        void getAllWalksSummary_shouldReturnPaginatedSummary_whenNoAuth() throws Exception {

            mockMvc.perform(get("/api/walks/public")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "createdAt")
                            .param("sortDirection", "ASC")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.content[0].title", is("Englischer Garten")));
        }

        @Test
        @DisplayName("GET api/walks/public/{id} should return walk when valid id and is active")
        void getWalkDetailById_shouldReturnWalk_whenExist() throws Exception {

            mockMvc.perform(get("/api/walks/public/{id}", walk.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(walk.getId().intValue())))
                    .andExpect(jsonPath("$.title", is("Englischer Garten")))
                    .andExpect(jsonPath("$.location", is("Munich")))
                    .andExpect(jsonPath("$.duration", is(120)))
                    .andExpect(jsonPath("$.description", is("Marvellous walk")))
                    .andExpect(jsonPath("$.username", is("Maria")));
        }

        @Test
        @DisplayName("GET /api/walks/public/{id} - must return 404 not found when invalid id")
        void getWalkById_shouldReturnNotFound_whenInvalidId() throws Exception{

            mockMvc.perform(get("/api/walks/public/{id}", 99L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/walks/public/{id} - must return 404 not found when walk is inactive")
        void getWalkById_shouldReturnNotFound_whenInactive() throws Exception{

            walk.setIsActive(false);
            walkRepository.save(walk);

            mockMvc.perform(get("/api/walks/public/{id}", walk.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Get Walks (Authenticated) Tests")
    class GetPrivateWalksTests {

        @Test
        @DisplayName("GET /api/walks - must return paginated walks when user authenticated")
        void getAllWalks_shouldReturnAllWalksPaginated_whenAdminAuthenticated() throws Exception {

            mockMvc.perform(get("/api/walks")
                            .header("Authorization", "Bearer " + userToken)
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "createdAt")
                            .param("sortDirection", "ASC")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[*].username", containsInAnyOrder("Maria")));
        }

        @Test
        @DisplayName("GET /api/walks - must return 401 unauthorized when no token provided")
        void getAllWalks_shouldReturnUnauthorized_whenNoToken() throws Exception {

            mockMvc.perform(get("/api/walks")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/walks/{id} - must return walk when user owns the walk")
        void getWalkById_shouldReturnWalk_whenUserIsOwner() throws Exception {

            mockMvc.perform(get("/api/walks/{id}", walk.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(walk.getId().intValue())))
                    .andExpect(jsonPath("$.title", is("Englischer Garten")))
                    .andExpect(jsonPath("$.isActive", is(true)))
                    .andExpect(jsonPath("$.username", is("Maria")));
        }

        @Test
        @DisplayName("GET /api/walks/{id} - must return walk when admin access any walk")
        void getWalkById_shouldReturnWalk_whenAdmin() throws Exception {

            mockMvc.perform(get("/api/walks/{id}", walk.getId())
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(walk.getId().intValue())))
                    .andExpect(jsonPath("$.title", is("Englischer Garten")));
        }

        @Test
        @DisplayName("GET /api/walks/{id} - must return 404 not found when invalid id")
        void getWalkById_shouldReturnNotFound_whenInvalidId() throws Exception{

            mockMvc.perform(get("/api/walk/{id}", 99L)
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Add Walks Tests")
    class AddWalkTests {

        @Test
        @DisplayName("POST /api/walks - must create a walk when valid request and authenticated user")
        void addWalk_shouldAddWalk_whenValidRequestAndAuth() throws Exception {

            WalkRequest request = new WalkRequest(
                    "new walk",
                    "Madrid",
                    60,
                    "ok",
                    "mad.png",
                    true
            );

            mockMvc.perform(post("/api/walks", walk.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(integrationTestUtils.toJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title", is("new walk")))
                    .andExpect(jsonPath("$.location", is("Madrid")))
                    .andExpect(jsonPath("$.duration", is(60)))
                    .andExpect(jsonPath("$.description", is("ok")))
                    .andExpect(jsonPath("$.username", is("Maria")))
                    .andExpect(jsonPath("$.isActive", is(true)));

            assertEquals(2, walkRepository.count());
        }
    }

    @Nested
    @DisplayName("Update Walks Tests")
    class UpdateWalkTests {

        @Test
        @DisplayName("PUT /api/walks/{id} - must update walk when user owns the walk")
        void updateWalkById_shouldUpdateWalk_whenUserIsOwner() throws Exception {

            WalkRequest request = new WalkRequest(
                    "new2 walk",
                    "Madrid",
                    60,
                    "ok",
                    "mad.png",
                    true
            );

            mockMvc.perform(put("/api/walks/{id}", walk.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(integrationTestUtils.toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("new2 walk")))
                    .andExpect(jsonPath("$.isActive", is(true)));
        }
    }

    @Nested
    @DisplayName("Delete Walks Tests")
    class DeleteWalkTests {

        @Test
        @DisplayName("Delete /api/walks/{id} - must delete walk when user owns the walk")
        void deleteWalk_shouldDeleteWalk_whenUserOwns() throws Exception{

            mockMvc.perform(delete("/api/walks/{id}", walk.getId())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            assertFalse(walkRepository.findById(walk.getId()).isPresent());
        }
    }
}
