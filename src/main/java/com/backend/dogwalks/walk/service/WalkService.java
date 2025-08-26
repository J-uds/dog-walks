package com.backend.dogwalks.walk.service;

import com.backend.dogwalks.exception.custom_exception.EntityNotFoundException;
import com.backend.dogwalks.user.dto.admin.AdminUserMapper;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.walk.dto.*;
import com.backend.dogwalks.walk.entity.Walk;
import com.backend.dogwalks.walk.repository.WalkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Set;

@Service
@Transactional
public class WalkService {

    private final WalkRepository walkRepository;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "title", "location", "duration", "createdAt", "isActive");

    public WalkService(WalkRepository walkRepository) {
        this.walkRepository = walkRepository;
    }

    @Transactional(readOnly = true)
    public Page<WalkSummaryResponse> getAllWalksSummary(int page, int size, String sortBy, String sortDirection) {

        if (page < 0) throw new IllegalArgumentException("Page index must be 0 or greater");
        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");

        int maxSize = 100;
        size = Math.min(size, maxSize);

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "createdAt";
        }

        if (!"ASC".equalsIgnoreCase(sortDirection) && !"DESC".equalsIgnoreCase(sortDirection)) {
            sortDirection = "ASC";
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return walkRepository.findByIsActiveTrue(pageable).map(walk -> WalkMapper.toSummaryDto(walk));
    }

    @Transactional(readOnly = true)
    public Page<WalkResponse> getAllWalksPaginated(int page, int size, String sortBy, String sortDirection) {

        if (page < 0) throw new IllegalArgumentException("Page index must be 0 or greater");
        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");

        int maxSize = 100;
        size = Math.min(size, maxSize);

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "createdAt";
        }

        if (!"ASC".equalsIgnoreCase(sortDirection) && !"DESC".equalsIgnoreCase(sortDirection)) {
            sortDirection = "ASC";
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return walkRepository.findAll(pageable).map(walk -> WalkMapper.toDto(walk));
    }

    @Transactional(readOnly = true)
    public WalkDetailResponse getWalkDetailById(Long id) {

        Walk walk = walkRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new EntityNotFoundException("Walk not found or inactive"));

        return WalkMapper.toDetailDto(walk);
    }

    @Transactional(readOnly = true)
    public WalkResponse getWalkById(Long id) {

        Walk walk = findById(id);

        return WalkMapper.toDto(walk);
    }

    public WalkResponse addWalk(WalkRequest request, CustomUser user) {

        Walk newWalk = WalkMapper.toEntity(request, user);
        Walk savedWalk = walkRepository.save(newWalk);

        return WalkMapper.toDto(savedWalk);
    }

    public WalkResponse updateWalk(Long id, WalkRequest request) {

        Walk walk = findById(id);

        WalkMapper.updateFromWalkRequest(walk, request);

        Walk updatedWalk = walkRepository.save(walk);

        return WalkMapper.toDto(updatedWalk);
    }

    public void deleteWalk(Long id) {

        Walk walk = findById(id);

        walkRepository.delete(walk);
    }

    private Walk findById(Long id) {

        return walkRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Walk with id: " + id + " not found"));
    }
}
