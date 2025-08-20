package com.backend.dogwalks.user.dto;

import com.backend.dogwalks.user.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminUserRequest(
        @Size(min = 2, max = 50, message = "Username must contain between 2 and 50 characters")
        String username,

        @Email(message = "Invalid email format", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        String email,

        @Pattern(message = "Password must be at least 12 characters long, including a number, one uppercase letter, one lowercase letter and one special character", regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=.])(?=\\S+$).{12,}$")
        String password,
        String userImgUrl,
        Role role
) {
}
