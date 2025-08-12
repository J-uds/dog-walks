package com.backend.dogwalks.auth.dto.register;

import com.backend.dogwalks.user.entity.CustomUser;

public class RegisterMapper {
    public static CustomUser toEntity(RegisterRequest registerRequest) {
        return new CustomUser(
              registerRequest.username(),
              registerRequest.email(),
              registerRequest.password(),
              registerRequest.userImgUrl()
        );
    }

    public static RegisterResponse toDto (CustomUser user) {
        return new RegisterResponse(user.getId(), user.getUsername(), user.getEmail(), user.getUserImgUrl(), user.getRole());
    }
}
