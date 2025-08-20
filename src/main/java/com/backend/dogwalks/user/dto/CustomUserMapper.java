package com.backend.dogwalks.user.dto;

import com.backend.dogwalks.user.entity.CustomUser;

public class CustomUserMapper {

    public static CustomUser toEntity(CustomUserRequest customUserRequest) {
        return new CustomUser(
                customUserRequest.username(),
                customUserRequest.email(),
                customUserRequest.password(),
                customUserRequest.userImgUrl()
        );
    }

    public static CustomUserResponse toDto(CustomUser user) {
        return new CustomUserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getUserImgUrl(), user.getRole());
    }
}
