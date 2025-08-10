package com.backend.dogwalks.user.repository;

import com.backend.dogwalks.user.entity.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomUserRepository extends JpaRepository <CustomUser, Long> {
}
