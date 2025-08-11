package com.backend.dogwalks.auth.dto;

import com.backend.dogwalks.user.enums.Role;

public record UserRegisterResponse (
        Long id,
        String username,
        String email,
        String userImgUrl,
        Role role
){
}
