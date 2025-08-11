package com.backend.dogwalks.user.dto;

import com.backend.dogwalks.auth.dto.UserRegistrationRequest;
import com.backend.dogwalks.user.entity.CustomUser;

public class UserMapper {
    public static CustomUser toEntity(UserRegistrationRequest userRegistrationRequest) {
        return new CustomUser(
              userRegistrationRequest.username(),
              userRegistrationRequest.email(),
              userRegistrationRequest.password(),
              userRegistrationRequest.userImgUrl()
        );
    }
}
