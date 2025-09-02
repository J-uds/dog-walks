package com.backend.dogwalks.walk;

import com.backend.dogwalks.exception.custom_exception.EntityNotFoundException;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.walk.dto.WalkDetailResponse;
import com.backend.dogwalks.walk.dto.WalkRequest;
import com.backend.dogwalks.walk.dto.WalkResponse;
import com.backend.dogwalks.walk.dto.WalkSummaryResponse;
import com.backend.dogwalks.walk.entity.Walk;
import com.backend.dogwalks.walk.repository.WalkRepository;
import com.backend.dogwalks.walk.service.WalkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Walk Service Unit Tests")
public class WalkServiceTest {

    @Mock
    private WalkRepository walkRepository;

    @InjectMocks
    private WalkService walkService;

    private Walk walk;
    private CustomUser user;

    @BeforeEach
    void setUp() {
        user = new CustomUser();
        user.setId(1L);
        user.setEmail("maria@test.com");

        walk = new Walk(
                "Englischer Garten",
                "Munich",
                120,
                "Marvellous walk",
                "eg.png",
                true,
                user
        );
        walk.setId(1L);
    }

    @Nested
    @DisplayName("Get Walks Tests")
    class GetWalksTests {

        @Test
        @DisplayName("GetAllWalksSummary should return a page of walks when request is valid")
        void getAllWalksSummary_shouldReturnPage_whenRequestIsValid() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt"));
            Page<Walk> walkPage = new PageImpl<>(List.of(walk), pageable, 1);

            when(walkRepository.findByIsActiveTrue(any(Pageable.class))).thenReturn(walkPage);

            Page<WalkSummaryResponse> result = walkService.getAllWalksSummary(0, 10, "createdAt", "ASC");

            assertEquals(1, result.getTotalElements());

            verify(walkRepository, times(1)).findByIsActiveTrue(any(Pageable.class));
        }

        @Test
        @DisplayName("GetAllWalksPaginated should return a page of walks when user is auth")
        void getAllWalksPaginated_shouldReturnPage_whenUserIsAuth() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
            Page<Walk> walkPage = new PageImpl<>(List.of(walk), pageable, 1);

            CustomUser user = new CustomUser();
            user.setId(1L);
            user.setRole(Role.USER);

            when(walkRepository.findByUser(eq(user), any(Pageable.class))).thenReturn(walkPage);

            Page<WalkResponse> result = walkService.getAllWalksPaginated(user, 0, 10, "id", "ASC");

            assertEquals(1, result.getTotalElements());

            verify(walkRepository, times(1)).findByUser(eq(user), any(Pageable.class));
        }

        @Test
        @DisplayName("GetAllWalksPaginated should return a page of walks when user is admin")
        void getAllWalksPaginated_shouldReturnPage_whenUserIsAdmin() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
            Page<Walk> walkPage = new PageImpl<>(List.of(walk), pageable, 1);

            CustomUser admin = new CustomUser();
            admin.setId(1L);
            admin.setRole(Role.ADMIN);

            when(walkRepository.findAll(any(Pageable.class))).thenReturn(walkPage);

            Page<WalkResponse> result = walkService.getAllWalksPaginated(admin,0, 10, "id", "ASC");

            assertEquals(1, result.getTotalElements());

            verify(walkRepository, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("GetWalkDetailById should return walk details when exist")
        void getWalkDetailById_shouldReturnWalk_whenExist() {

            when(walkRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(walk));

            WalkDetailResponse result = walkService.getWalkDetailById(1L);

            assertNotNull(result);
            assertEquals("Englischer Garten", result.title());

            verify(walkRepository, times(1)).findByIdAndIsActiveTrue(1L);
        }

        @Test
        @DisplayName("GetWalkDetailById should throw entity not found exception when walk not found")
        void getWalkDetailById_shouldThrowException_whenNotFound() {

            when(walkRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> walkService.getWalkDetailById(1L));

            assertEquals("Walk not found or inactive", exception.getMessage());

            verify(walkRepository, times(1)).findByIdAndIsActiveTrue(1L);
        }

        @Test
        @DisplayName("GetWalkById should return walk when exist")
        void getWalkById_shouldReturnWalk_whenExist() {

            when(walkRepository.findById(1L)).thenReturn(Optional.of(walk));

            WalkResponse result = walkService.getWalkById(1L);

            assertNotNull(result);
            assertEquals("Englischer Garten", result.title());

            verify(walkRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("GetWalkById should throw entity not found exception when walk not found")
        void getWalkById_shouldThrowException_whenNotFound() {

            when(walkRepository.findById(1L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> walkService.getWalkById(1L));

            assertEquals("Walk with id: 1 not found", exception.getMessage());

            verify(walkRepository, times(1)).findById(1L);
        }
    }

    @Nested
    @DisplayName("Add Walk Tests")
    class AddWalkTests {

        @Test
        @DisplayName("AddWalk should save walk when request is valid")
        void addWalk_shouldSaveWalk_whenRequestValid() {

            WalkRequest request = new WalkRequest("Englischer Garten","Munich", 120,"Marvellous walk","eg.png",true);

            when(walkRepository.save(any(Walk.class))).thenReturn(walk);

            WalkResponse result = walkService.addWalk(request, user);

            assertNotNull(result);
            assertEquals("Englischer Garten", request.title());
            verify(walkRepository, times(1)).save(any(Walk.class));
        }
    }

    @Nested
    @DisplayName("Update Walk Tests")
    class UpdateWalkTests {

        @Test
        @DisplayName("UpdateWalk should update walk when walk exist")
        void updateWalk_shouldUpdateWalk_whenWalkExist() {

            WalkRequest request = new WalkRequest("Updated walk","Munich", 120,"Marvellous walk","eg.png",true);

            when(walkRepository.findById(1L)).thenReturn(Optional.of(walk));
            when(walkRepository.save(any(Walk.class))).thenReturn(walk);

            WalkResponse result = walkService.updateWalk(1L, request);

            assertNotNull(result);
            assertEquals("Updated walk", result.title());

            verify(walkRepository, times(1)).findById(1L);
            verify(walkRepository, times(1)).save(any(Walk.class));
        }

        @Test
        @DisplayName("UpdateWalk should throw entity not found exception when walk not found")
        void updateWalk_shouldThrowException_whenNotFound() {

            WalkRequest request = new WalkRequest("Updated walk","Munich", 120,"Marvellous walk","eg.png",true);

            when(walkRepository.findById(1L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> walkService.updateWalk(1L, request));

            assertEquals("Walk with id: 1 not found", exception.getMessage());

            verify(walkRepository, times(1)).findById(1L);
            verify(walkRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Walk Tests")
    class DeleteWalkTests {

        @Test
        @DisplayName("DeleteWalk should delete walk when walk exist")
        void deleteWalk_shouldDeleteWalk_whenWalkExist() {

            when(walkRepository.findById(1L)).thenReturn(Optional.of(walk));

            walkService.deleteWalk(1L);

            verify(walkRepository, times(1)).findById(1L);
            verify(walkRepository, times(1)).delete(walk);
        }

        @Test
        @DisplayName("DeleteWalk should throw entity not found exception when walk not found")
        void deleteWalk_shouldThrowException_whenNotFound() {

            when(walkRepository.findById(1L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> walkService.deleteWalk(1L));

            assertEquals("Walk with id: 1 not found", exception.getMessage());

            verify(walkRepository, times(1)).findById(1L);
        }
    }

}
