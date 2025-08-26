package com.backend.dogwalks.walk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record WalkDetailResponse(
        Long id,
        String title,
        String location,
        Integer duration,
        String description,
        String walkImgUrl,
        String username,

        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime createAt
) {
}
