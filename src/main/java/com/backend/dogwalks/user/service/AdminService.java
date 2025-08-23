package com.backend.dogwalks.user.service;

import com.backend.dogwalks.exception.custom_exception.EntityAlreadyExistsException;
import com.backend.dogwalks.exception.custom_exception.EntityNotFoundException;
import com.backend.dogwalks.user.dto.admin.AdminUserMapper;
import com.backend.dogwalks.user.dto.admin.AdminUserRequest;
import com.backend.dogwalks.user.dto.admin.AdminUserResponse;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.IllegalStateException;

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

    @Transactional(readOnly = true)
    public AdminUserResponse getUserById (Long id) {

        CustomUser user = findById(id);

        return AdminUserMapper.toDto(user);
    }

    public AdminUserResponse updateUser(Long id, AdminUserRequest request) {

        CustomUser user = findById(id);

        validateUniqueFields(request, id);
        validateRoleChange(user, request.role());
        validateIsActiveChange(user, request.isActive());

        AdminUserMapper.updateFromAdminRequest(user, request);

        CustomUser savedUser = customUserRepository.save(user);

        return AdminUserMapper.toDto(savedUser);
    }

    public void deleteUser(Long id) {

        CustomUser user = findById(id);

        validateNotLastAdmin(user);

        customUserRepository.delete(user);
    }

    private void validateUniqueFields(AdminUserRequest request, Long id) {

        if (request.email() != null && customUserRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new EntityAlreadyExistsException("E-mail " + request.email() + " is already registered");
        }
    }

    private void validateRoleChange(CustomUser user, Role newRole) {

        if (newRole != null && user.getRole() == Role.ADMIN && newRole != Role.ADMIN) {

            long adminCount = customUserRepository.countByRoleAndIsActive(Role.ADMIN, true);

            if(adminCount <= 1) {
                throw new IllegalStateException("Cannot change the role of the last active admin");
            }
        }
    }

    private void validateIsActiveChange(CustomUser user, Boolean newIsActive ) {

        if (newIsActive != null && !newIsActive && user.getRole() == Role.ADMIN && user.getIsActive()) {

            long activeAdminCount = customUserRepository.countByRoleAndIsActive(Role.ADMIN, true);

            if (activeAdminCount <= 1) {
                throw new IllegalStateException("Cannot deactivate the last active admin");
            }
        }
    }

    private void validateNotLastAdmin(CustomUser user) {
        if (user.getRole() == Role.ADMIN) {

            long adminCount = customUserRepository.countByRoleAndIsActive(Role.ADMIN, true);

            if (adminCount <= 1) {
                throw new IllegalStateException("Cannot delete the last active admin");
            }
        }
    }

    private CustomUser findById(Long id) {

        return customUserRepository.findById(id). orElseThrow(() -> new EntityNotFoundException("User with id: " + id + " not found"));
    }

}
