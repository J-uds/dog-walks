package com.backend.dogwalks.user.dto.admin;

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

    public static void updateFromAdminRequest(CustomUser user, AdminUserRequest request) {
        if (request.username() != null) {
            user.setUsername(request.username());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.userImgUrl() != null) {
            user.setUserImgUrl(request.userImgUrl());
        }
        if (request.role() != null) {
            user.setRole(request.role());
        }
        if (request.isActive() != null) {
            user.setIsActive(request.isActive());
        }
    }
}
