package com.backend.dogwalks.user.dto;

import com.backend.dogwalks.user.enums.Role;

public record CustomUserResponse(
        Long id,
        String username,
        String email,
        String userImgUrl,
        Role role
) {
}
