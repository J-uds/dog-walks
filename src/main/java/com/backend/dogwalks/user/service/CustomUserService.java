package com.backend.dogwalks.user.service;

import com.backend.dogwalks.exception.custom_exception.EntityAlreadyExistsException;
import com.backend.dogwalks.exception.custom_exception.EntityNotFoundException;
import com.backend.dogwalks.exception.custom_exception.InvalidCredentialsException;
import com.backend.dogwalks.user.dto.user.*;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomUserService {

    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserService(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public CustomUserResponse getMyProfile(Long id) {

        CustomUser user = findActiveUserById(id);

        return CustomUserMapper.toDto(user);
    }

    public CustomUserResponse updateMyProfile(Long id, CustomUserUpdateRequest request) {

        CustomUser user = findActiveUserById(id);

        CustomUserMapper.updateFromCustomUserUpdateRequest(user, request);

        CustomUser updatedUser = customUserRepository.save(user);

        return CustomUserMapper.toDto(updatedUser);
    }

    public CustomUserResponse updateMyEmail(Long id, CustomUserUpdateEmailRequest request) {

        CustomUser user = findActiveUserById(id);

        validateEmailChange(request, user);

        user.setEmail(request.newEmail());

        CustomUser updatedUser = customUserRepository.save(user);

        return CustomUserMapper.toDto(updatedUser);
    }

    public void updateMyPassword(Long id, CustomUserUpdatePasswordRequest request) {

        CustomUser user = findActiveUserById(id);

        validatePasswordChange(request, user);

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        customUserRepository.save(user);
    }

    public void deactivateMyProfile(Long id) {

        CustomUser user = findActiveUserById(id);

        user.setIsActive(false);

        customUserRepository.save(user);
    }

    private void validateEmailChange(CustomUserUpdateEmailRequest request, CustomUser user) {

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid Password");
        }

        if (customUserRepository.existsByEmail(request.newEmail())) {
            throw new EntityAlreadyExistsException("E-mail " + request.newEmail() + " is already registered");
        }

        if (user.getEmail().equals(request.newEmail())) {
            throw new InvalidCredentialsException("New e-amil must be different from the current e-mail");
        }
    }

    private void validatePasswordChange(CustomUserUpdatePasswordRequest request, CustomUser user) {

        if(!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException(("Current password is incorrect"));
        }

        if(!request.newPassword().equals(request.confirmPassword())) {
            throw new InvalidCredentialsException("New Password and confirmation do not match");
        }

        if(passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("New password must be different from current password");
        }
    }

    private CustomUser findActiveUserById(Long id) {
        return customUserRepository.findByIdAndIsActive(id, true).orElseThrow(() -> new EntityNotFoundException("Active user with id: " + id + " not found"));
    }
}
