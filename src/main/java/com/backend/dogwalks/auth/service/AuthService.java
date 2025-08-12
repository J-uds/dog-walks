package com.backend.dogwalks.auth.service;

import com.backend.dogwalks.auth.dto.login.LoginRequest;
import com.backend.dogwalks.auth.dto.login.LoginResponse;
import com.backend.dogwalks.auth.dto.register.RegisterMapper;
import com.backend.dogwalks.auth.dto.register.RegisterRequest;
import com.backend.dogwalks.auth.dto.register.RegisterResponse;
import com.backend.dogwalks.exception.custom_exception.EntityAlreadyExistsException;
import com.backend.dogwalks.security.CustomUserDetails;
import com.backend.dogwalks.security.jwt.JwtUtil;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public RegisterResponse registerUser(RegisterRequest request) {
        if (customUserRepository.existsByEmail(request.email())) {
            throw new EntityAlreadyExistsException("E-mail " + request.email() + " is already registered");
        }

        CustomUser newUser = RegisterMapper.toEntity(request);
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(Role.USER);
        CustomUser saveUser = customUserRepository.save(newUser);

        return RegisterMapper.toDto(saveUser);
    }

    public LoginResponse loginUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        CustomUserDetails userDetails =  (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtil.generateToken(userDetails);

        CustomUser user = customUserRepository.findUserByEmail(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException("Authenticated user with e-mail: " + userDetails.getUsername() + ", not found in data base"));

       return new LoginResponse(token,"Bearer", user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}
