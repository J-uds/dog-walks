package com.backend.dogwalks.walk.service;

import com.backend.dogwalks.exception.custom_exception.EntityNotFoundException;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.walk.dto.WalkMapper;
import com.backend.dogwalks.walk.dto.WalkRequest;
import com.backend.dogwalks.walk.dto.WalkResponse;
import com.backend.dogwalks.walk.entity.Walk;
import com.backend.dogwalks.walk.repository.WalkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WalkService {

    private final WalkRepository walkRepository;

    public WalkService(WalkRepository walkRepository) {
        this.walkRepository = walkRepository;
    }

    @Transactional(readOnly = true)
    public Page<WalkResponse> getAllWalksPaginated(int page, int size, String sortBy, String sortDirection) {

        int maxSize = 100;
        size = Math.min(size, maxSize);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return walkRepository.findAll(pageable).map(walk -> WalkMapper.toDto(walk));
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

        walk.setTitle(request.title());
        walk.setLocation(request.location());
        walk.setDuration(request.duration());
        walk.setDescription(request.description());
        walk.setWalkImgUrl(request.walkImgUrl());
        walk.setIsActive(request.isActive());

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
