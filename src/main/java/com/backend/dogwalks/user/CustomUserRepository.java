package com.backend.dogwalks.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomUserRepository extends JpaRepository <CustomUser, Long> {
}
