package com.backend.dogwalks.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 2, max = 50, message = "Username must contain between 2 and 50 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 12, message = "Password must be at least 12 characters long")
        String password,

        String userImgUrl
) {
}
