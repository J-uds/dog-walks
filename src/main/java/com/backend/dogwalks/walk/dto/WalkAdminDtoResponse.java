package com.backend.dogwalks.walk.dto;

public record WalkAdminResponse(
        Long id,
        String title,
        String location
) {
}
