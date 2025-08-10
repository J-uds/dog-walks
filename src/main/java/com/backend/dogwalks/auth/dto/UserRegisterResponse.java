package com.backend.dogwalks.auth.dto;

public record UserRegisterResponse (
        Long id,
        String username,
        String email,
        String role
){
}
