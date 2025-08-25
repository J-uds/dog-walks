package com.backend.dogwalks.user.dto.admin;

import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.walk.dto.WalkAdminResponse;

import java.util.List;

public record AdminUserResponse(
        Long id,
        String username,
        String email,
        String userImgUrl,
        Role role,
        Boolean isActive,
        List<WalkAdminResponse> walks
) {
}
