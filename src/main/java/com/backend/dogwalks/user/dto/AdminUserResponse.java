package com.backend.dogwalks.user.dto;

import com.backend.dogwalks.user.enums.Role;

public record AdminUserResponse(
        Long id,
        String username,
        String email,
        String userImgUrl,
        Role role,
        Boolean isActive
) {
}
