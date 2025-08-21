package com.backend.dogwalks.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomUserUpdatePasswordRequest(

        @NotBlank
        @Pattern(message = "Password must be at least 12 characters long, including a number, one uppercase letter, one lowercase letter and one special character", regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=.])(?=\\S+$).{12,}$")
        String newPassword,

        @NotBlank
        String oldPassword
) {
}
