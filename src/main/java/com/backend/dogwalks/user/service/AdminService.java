package com.backend.dogwalks.user.service;

import com.backend.dogwalks.user.dto.admin.AdminUserMapper;
import com.backend.dogwalks.user.dto.admin.AdminUserResponse;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminService {

    private final CustomUserRepository customUserRepository;

    public AdminService(CustomUserRepository customUserRepository) {
        this.customUserRepository = customUserRepository;
    }

    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAllUsersPaginated(int page, int size, String sortBy, String sortDirection) {

        int maxSize = 100;
        if (size > maxSize) {
            size = maxSize;
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return customUserRepository.findAll(pageable).map(user -> AdminUserMapper.toDto(user));
    }
}
