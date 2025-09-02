package com.backend.dogwalks.user.dto.user;

public record CustomUserResponse(
        Long id,
        String username,
        String email,
        String userImgUrl
) {
}
