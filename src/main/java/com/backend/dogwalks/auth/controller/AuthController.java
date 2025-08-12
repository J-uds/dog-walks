package com.backend.dogwalks.auth.controller;

import com.backend.dogwalks.auth.dto.register.RegisterRequest;
import com.backend.dogwalks.auth.dto.register.RegisterResponse;
import com.backend.dogwalks.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.registerUser(request), HttpStatus.CREATED);
    }
}
