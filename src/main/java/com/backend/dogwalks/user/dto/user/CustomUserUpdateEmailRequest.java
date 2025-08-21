package com.backend.dogwalks.user.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomUserUpdateEmailRequest(

        @NotBlank(message = "New e-mail is required")
        @Email(message = "Invalid email format", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        String newEmail,

        @NotBlank(message = "Password is required to change the e-mail")
        String password
) {
}
