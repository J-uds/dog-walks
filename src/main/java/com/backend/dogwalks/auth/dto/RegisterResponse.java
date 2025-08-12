package com.backend.dogwalks.auth.dto;

import com.backend.dogwalks.user.enums.Role;

public record RegisterResponse(
        Long id,
        String username,
        String email,
        String userImgUrl,
        Role role
){
}
