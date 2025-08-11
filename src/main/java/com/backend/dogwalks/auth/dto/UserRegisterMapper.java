package com.backend.dogwalks.auth.dto;

import com.backend.dogwalks.user.entity.CustomUser;

public class UserRegisterMapper {
    public static CustomUser toEntity(UserRegisterRequest userRegisterRequest) {
        return new CustomUser(
              userRegisterRequest.username(),
              userRegisterRequest.email(),
              userRegisterRequest.password(),
              userRegisterRequest.userImgUrl()
        );
    }

    public static UserRegisterResponse toDto (CustomUser user) {
        return new UserRegisterResponse(user.getId(), user.getUsername(), user.getEmail(), user.getUserImgUrl(), user.getRole());
    }
}
