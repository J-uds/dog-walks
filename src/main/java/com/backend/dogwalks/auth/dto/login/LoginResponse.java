package com.backend.dogwalks.auth.dto.login;

import com.backend.dogwalks.user.enums.Role;

public record LoginResponse(
        String token,
        String tokenType,
        String username,
        String email,
        Role role
) {
}
