
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
}

