package com.backend.dogwalks.auth.service;

import com.backend.dogwalks.auth.dto.UserRegisterMapper;
import com.backend.dogwalks.auth.dto.UserRegisterRequest;
import com.backend.dogwalks.auth.dto.UserRegisterResponse;
import com.backend.dogwalks.exception.custom_exception.EntityAlreadyExistsException;
import com.backend.dogwalks.user.entity.CustomUser;
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

    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        if (customUserRepository.existsByEmail(request.email())) {
            throw new EntityAlreadyExistsException("E-mail " + request.email() + " is already registered");
        }

        CustomUser newUser = UserRegisterMapper.toEntity(request);
        newUser.setPassword(passwordEncoder.encode(request.password()));
        CustomUser saveUser = customUserRepository.save(newUser);

        return UserRegisterMapper.toDto(saveUser);
    }
}
