package com.backend.dogwalks.user.dto.user;

import jakarta.validation.constraints.Size;

public record CustomUserUpdateRequest(

        @Size(min = 2, max = 50, message = "Username must contain between 2 and 50 characters")
        String username,
        String userImgUrl
) {
}
