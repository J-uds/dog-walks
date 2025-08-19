package com.backend.dogwalks.auth.service;

import com.backend.dogwalks.auth.dto.login.LoginRequest;
import com.backend.dogwalks.auth.dto.login.LoginResponse;
import com.backend.dogwalks.auth.dto.register.RegisterRequest;
import com.backend.dogwalks.auth.dto.register.RegisterResponse;
import com.backend.dogwalks.exception.custom_exception.EntityAlreadyExistsException;
import com.backend.dogwalks.exception.custom_exception.InvalidCredentialsException;
import com.backend.dogwalks.security.CustomUserDetails;
import com.backend.dogwalks.security.jwt.JwtUtil;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private CustomUserRepository customUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

   @Test
    void registerUser_shouldSaveUser_whenEmailNotExist() {

       RegisterRequest request = new RegisterRequest("Maria", "maria@test.com", "Maria123456.", "img.png");
       CustomUser savedUser = new CustomUser(1L, "Maria", "maria@test.com", "encodedPassword","img.png", Role.USER);

       when(customUserRepository.existsByEmail("maria@test.com")).thenReturn(false);
       when(passwordEncoder.encode("Maria123456.")).thenReturn("encodedPassword");
       when(customUserRepository.save(any(CustomUser.class))).thenReturn(savedUser);

       RegisterResponse response = authService.registerUser(request);

       assertNotNull(response);
       assertEquals(1L, response.id());
       assertEquals("Maria", response.username());
       assertEquals("maria@test.com", response.email());
       assertEquals("img.png", response.userImgUrl());
       assertEquals(Role.USER, response.role());

       verify(customUserRepository, times(1)).save(any(CustomUser.class));
   }

   @Test
    void registerUser_shouldThrowException_whenEmailAlreadyExist() {

       RegisterRequest request = new RegisterRequest("Maria", "maria@test.com", "Maria123456.", "img.png");

       when(customUserRepository.existsByEmail("maria@test.com")).thenReturn(true);

       assertThrows(EntityAlreadyExistsException.class, () -> authService.registerUser(request));

       verify(customUserRepository, never()).save(any());
   }

   @Test
    void loginUser_shouldReturnToken_whenValidCredentials() {

       LoginRequest request = new LoginRequest("maria@test.com", "Maria123456.");
       CustomUser user = new CustomUser(1L, "Maria", "maria@test.com", "encodedPassword","img.png", Role.USER);
       CustomUserDetails userDetails = new CustomUserDetails(user);
       Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

       when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
       when(jwtUtil.generateToken(userDetails)).thenReturn("test-jwt-token");
       when(customUserRepository.findUserByEmail("maria@test.com")).thenReturn(Optional.of(user));

       LoginResponse response = authService.loginUser(request);

       assertNotNull(response);
       assertEquals("test-jwt-token", response.token());
       assertEquals("Bearer", response.tokenType());
       assertEquals("maria@test.com", response.email());
   }

   @Test
    void loginUser_shouldThrowException_whenInvalidCredentials() {

       LoginRequest request = new LoginRequest("bad@test.com", "wrongPassword");

       when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

       assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(request));
   }
}
