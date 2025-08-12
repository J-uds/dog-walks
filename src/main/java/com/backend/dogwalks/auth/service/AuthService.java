package com.backend.dogwalks.auth.service;

import com.backend.dogwalks.auth.dto.register.RegisterMapper;
import com.backend.dogwalks.auth.dto.register.RegisterRequest;
import com.backend.dogwalks.auth.dto.register.RegisterResponse;
import com.backend.dogwalks.exception.custom_exception.EntityAlreadyExistsException;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
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
}
