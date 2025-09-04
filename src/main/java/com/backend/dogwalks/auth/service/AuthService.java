package com.backend.dogwalks.auth.service;

import com.backend.dogwalks.auth.dto.login.LoginMapper;
import com.backend.dogwalks.auth.dto.login.LoginRequest;
import com.backend.dogwalks.auth.dto.login.LoginResponse;
import com.backend.dogwalks.auth.dto.register.RegisterMapper;
import com.backend.dogwalks.auth.dto.register.RegisterRequest;
import com.backend.dogwalks.auth.dto.register.RegisterResponse;
import com.backend.dogwalks.exception.custom_exception.EntityAlreadyExistsException;
import com.backend.dogwalks.exception.custom_exception.InvalidCredentialsException;
import com.backend.dogwalks.exception.custom_exception.UserNotActiveException;
import com.backend.dogwalks.security.user.CustomUserDetails;
import com.backend.dogwalks.security.user.jwt.JwtUtil;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import com.backend.dogwalks.user.service.CustomUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final CustomUserRepository customUserRepository;
    private final CustomUserService customUserService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(CustomUserRepository customUserRepository, CustomUserService customUserService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.customUserRepository = customUserRepository;
        this.customUserService = customUserService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
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
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            CustomUser user = customUserService.findUserByEmail(userDetails.getUsername());

            if (!user.getIsActive()) {
                throw new UserNotActiveException("User account is deactivated");
            }

            String token = jwtUtil.generateToken(userDetails);
            String tokenType = "Bearer";

            return LoginMapper.toDto(token, tokenType, user);
        } catch (BadCredentialsException exception) {
            throw new InvalidCredentialsException("Incorrect e-mail or password");
        }
    }
}
