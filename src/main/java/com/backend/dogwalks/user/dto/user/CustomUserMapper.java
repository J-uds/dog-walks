
package com.backend.dogwalks.user.dto.user;

import com.backend.dogwalks.user.entity.CustomUser;

public class CustomUserMapper {

    /*public static CustomUser toEntity(CustomUserUpdateRequest customUserRequest) {
        return new CustomUser(
                customUserRequest.username(),
                customUserRequest.userImgUrl()
        );
    }*/

    public static CustomUserResponse toDto(CustomUser user) {
        return new CustomUserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getUserImgUrl());
    }

    public static void updateFromCustomUserUpdateRequest(CustomUser user, CustomUserUpdateRequest request) {
        if (request.username() != null) {
            user.setUsername(request.username());
        }

        if (request.userImgUrl() != null) {
            user.setUserImgUrl(request.userImgUrl());
        }
    }
}

