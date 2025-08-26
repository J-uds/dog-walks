package com.backend.dogwalks.walk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record WalkSummaryResponse(
        Long id,
        String title,
        String location,
        Integer duration,
        String walkImgUrl,

        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime createAt
) {
}
