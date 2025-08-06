package com.backend.dogwalks.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomUserRepository extends JpaRepository <CustomUser, Long> {
    Optional<CustomUser> findUserByEmail(String email);
}
