package com.backend.dogwalks.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomUserUpdateEmailRequest(

        @NotBlank
        @Email(message = "Invalid email format", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        String email
) {
}
