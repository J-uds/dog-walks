package com.backend.dogwalks.user.dto;

public record CustomUserResponse(
        Long id,
        String username,
        String email,
        String userImgUrl
) {
}
