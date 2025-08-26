package com.backend.dogwalks.walk.repository;

import com.backend.dogwalks.walk.entity.Walk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.Optional;

@Repository
public interface WalkRepository extends JpaRepository <Walk, Long> {
    Page<Walk> findByIsActiveTrue(Pageable pageable);
    Optional<Walk> findByIdAndIsActiveTrue(Long id);
}
