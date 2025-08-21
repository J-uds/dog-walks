package com.backend.dogwalks.user.dto;

import com.backend.dogwalks.user.entity.CustomUser;

public class AdminUserMapper {
    public static CustomUser toEntity(AdminUserRequest adminUserRequest) {
        return new CustomUser(
                adminUserRequest.username(),
                adminUserRequest.email(),
                adminUserRequest.userImgUrl(),
                adminUserRequest.role(),
                adminUserRequest.isActive()
        );
    }

    public static AdminUserResponse toDto(CustomUser user) {
        return new AdminUserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getUserImgUrl(), user.getRole(), user.getIsActive());
    }
}
