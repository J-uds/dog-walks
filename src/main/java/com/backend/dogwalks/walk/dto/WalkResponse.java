package com.backend.dogwalks.walk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record WalkResponse(
    Long id,
    String title,

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime createdAt,
    String location,
    Integer duration,
    String description,
    Boolean isActive,
    String username
) {
}
