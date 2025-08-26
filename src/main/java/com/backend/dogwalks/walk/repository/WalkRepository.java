package com.backend.dogwalks.walk.repository;

import com.backend.dogwalks.walk.entity.Walk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkRepository extends JpaRepository <Walk, Long> {
}
