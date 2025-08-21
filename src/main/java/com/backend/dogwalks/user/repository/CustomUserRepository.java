package com.backend.dogwalks.user.repository;

import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomUserRepository extends JpaRepository <CustomUser, Long> {
    Optional<CustomUser> findUserByEmail(String email);
    boolean existsByEmail(String email);
    Optional<CustomUser> findByUsername(String username);
    int countByRole(Role role);
    Optional<CustomUser> findByIdAndIsActive(Long id, Boolean isActive);
    Optional<CustomUser> findById(Long id);
}
