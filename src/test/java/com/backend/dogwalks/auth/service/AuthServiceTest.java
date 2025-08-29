package com.backend.dogwalks.auth.service;

import com.backend.dogwalks.auth.dto.login.LoginRequest;
import com.backend.dogwalks.auth.dto.login.LoginResponse;
import com.backend.dogwalks.auth.dto.register.RegisterRequest;
import com.backend.dogwalks.auth.dto.register.RegisterResponse;
import com.backend.dogwalks.exception.custom_exception.EntityAlreadyExistsException;
import com.backend.dogwalks.exception.custom_exception.EntityNotFoundException;
import com.backend.dogwalks.exception.custom_exception.InvalidCredentialsException;
import com.backend.dogwalks.exception.custom_exception.UserNotActiveException;
import com.backend.dogwalks.security.user.CustomUserDetails;
import com.backend.dogwalks.security.user.jwt.JwtUtil;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import com.backend.dogwalks.user.service.CustomUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("AuthService Unit Tests")
public class AuthServiceTest {

    @Mock
    private CustomUserRepository customUserRepository;

    @Mock
    private CustomUserService customUserService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private CustomUser mockUser;
    private CustomUserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("Maria", "maria@test.com", "Maria123456."/*"img.png"*/);
        loginRequest = new LoginRequest("maria@test.com", "Maria123456.");
        mockUser = new CustomUser();
        mockUser.setId(1L);
        mockUser.setUsername("Maria");
        mockUser.setEmail("maria@test.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setUserImgUrl("img.png");
        mockUser.setRole(Role.USER);
        mockUser.setIsActive(true);

        mockUserDetails = new CustomUserDetails(mockUser);
    }

   @Test
   @DisplayName("Register Should successfully register user when e-mail doesn't exist")
    void registerUser_shouldSaveUser_whenEmailNotExist() {

       when(customUserRepository.existsByEmail(registerRequest.email())).thenReturn(false);
       when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
       when(customUserRepository.save(any(CustomUser.class))).thenReturn(mockUser);

       RegisterResponse response = authService.registerUser(registerRequest);

       assertNotNull(response);
       assertEquals(1L, response.id());
       assertEquals("Maria", response.username());
       assertEquals("maria@test.com", response.email());
       /*assertEquals("img.png", response.userImgUrl());*/
       assertEquals(Role.USER, response.role());
       assertEquals(Boolean.TRUE, response.isActive());

       verify(customUserRepository, times(1)).existsByEmail(registerRequest.email());
       verify(passwordEncoder, times(1)).encode(registerRequest.password());
       verify(customUserRepository, times(1)).save(any(CustomUser.class));

       verify(customUserRepository).save(argThat(user ->
               user.getUsername().equals("Maria") &&
               user.getEmail().equals("maria@test.com") &&
               user.getPassword().equals("encodedPassword") &&
               /*user.getUserImgUrl().equals("img.png") &&*/
               user.getRole().equals(Role.USER) &&
               Boolean.TRUE.equals(user.getIsActive())));
   }

   @Test
   @DisplayName("Register Should throw EntityAlreadyExistException when e-mail already exist")
    void registerUser_shouldThrowException_whenEmailAlreadyExist() {

       when(customUserRepository.existsByEmail(registerRequest.email())).thenReturn(true);

       EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () -> authService.registerUser(registerRequest));

       assertEquals("E-mail maria@test.com is already registered", exception.getMessage());

       verify(customUserRepository, times(1)).existsByEmail(registerRequest.email());
       verify(passwordEncoder, never()).encode(anyString());
       verify(customUserRepository, never()).save(any(CustomUser.class));
   }

   @Test
   @DisplayName("Login Should return valid token when credentials are correct")
    void loginUser_shouldReturnToken_whenValidCredentials() {

       Authentication authentication = new UsernamePasswordAuthenticationToken(mockUserDetails, null);

       when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
       when(jwtUtil.generateToken(mockUserDetails)).thenReturn("test-jwt-token");
       when(customUserService.findUserByEmail(loginRequest.email())).thenReturn(mockUser);

       LoginResponse response = authService.loginUser(loginRequest);

       assertNotNull(response);
       assertEquals("test-jwt-token", response.token());
       assertEquals("Bearer", response.tokenType());
       assertEquals(1L, response.id());
       assertEquals("Maria", response.username());
       assertEquals("maria@test.com", response.email());
       assertEquals(Role.USER, response.role());
       assertEquals(Boolean.TRUE, response.isActive());

       verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
       verify(jwtUtil, times(1)).generateToken(mockUserDetails);
       verify(customUserService, times(1)).findUserByEmail(loginRequest.email());
   }

   @Test
   @DisplayName("Login Should throw InvalidCredentialsException when credentials are wrong")
    void loginUser_shouldThrowException_whenInvalidCredentials() {

       when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

       InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(loginRequest));

       assertEquals("Incorrect e-mail or password", exception.getMessage());

       verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
       verify(jwtUtil, never()).generateToken(any(CustomUserDetails.class));
       verify(customUserRepository, never()).findUserByEmail(anyString());
   }

   @Test
   @DisplayName("Login Should throw EntityNotFoundException when authenticated user not found in database")
    void loginUser_shouldThrowException_whenUserNotFoundAfterAuthentication() {

        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUserDetails, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(customUserService.findUserByEmail(loginRequest.email())).thenThrow(new EntityNotFoundException("User with e-mail: maria@test.com, not found in data base"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> authService.loginUser(loginRequest));

        assertEquals("User with e-mail: " + loginRequest.email() + ", not found in data base", exception.getMessage());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserService, times(1)).findUserByEmail(eq(loginRequest.email()));
        verifyNoInteractions(jwtUtil);
   }

   @Test
   @DisplayName("Login Should throw UserNotActiveException when user is deactivated")
   void loginUser_shouldThrowException_whenUserIsNotActive() {

       Authentication authentication = new UsernamePasswordAuthenticationToken(mockUserDetails, null);

       CustomUser inactiveUser = new CustomUser();
       inactiveUser.setIsActive(false);

       when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
       when(customUserService.findUserByEmail(loginRequest.email())).thenReturn(inactiveUser);

       UserNotActiveException exception = assertThrows(UserNotActiveException.class, () -> authService.loginUser(loginRequest));

       assertEquals("User account is deactivated", exception.getMessage());

       verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
       verify(customUserService, times(1)).findUserByEmail(eq(loginRequest.email()));
       verifyNoInteractions(jwtUtil);
   }

   @Test
   @DisplayName("Login Should handle JWT generation failure")
    void loginUser_shouldThrowException_whenJwtGenerationFails() {

        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUserDetails, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(customUserService.findUserByEmail(loginRequest.email())).thenReturn(mockUser);
        when(jwtUtil.generateToken(mockUserDetails)).thenThrow(new RuntimeException("JWT generation failed"));

        assertThrows(RuntimeException.class, () -> authService.loginUser(loginRequest));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserService, times(1)).findUserByEmail(eq(loginRequest.email()));
        verify(jwtUtil, times(1)).generateToken(mockUserDetails);
    }

   @Test
   @DisplayName("Login Should create authentication token with correct credentials")
    void loginUser_shouldCreateAuthenticationTokenWithCorrectCredentials() {

       Authentication authentication = new UsernamePasswordAuthenticationToken(mockUserDetails, null);

       when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
       when(jwtUtil.generateToken(mockUserDetails)).thenReturn("test-jwt-token");
       when(customUserService.findUserByEmail(loginRequest.email())).thenReturn(mockUser);

       authService.loginUser(loginRequest);

       verify(authenticationManager, times(1)).authenticate(argThat(token ->
               token instanceof UsernamePasswordAuthenticationToken &&
               token.getPrincipal().equals(loginRequest.email()) &&
               token.getCredentials().equals(loginRequest.password())));
   }
}
