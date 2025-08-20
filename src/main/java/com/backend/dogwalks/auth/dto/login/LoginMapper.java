package com.backend.dogwalks.auth.dto.login;

import com.backend.dogwalks.user.entity.CustomUser;

public class LoginMapper {

    public static LoginResponse toDto(String token, String tokenType, CustomUser user) {
        return new LoginResponse(token, tokenType, user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getIsActive());
    }
}
