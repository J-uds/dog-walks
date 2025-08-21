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

        CustomUser user = getActiveUserByIdOrThrow(id);

        return CustomUserMapper.toDto(user);
    }

    public CustomUserResponse updateMyProfile(Long id, CustomUserUpdateRequest request) {

        CustomUser user = getActiveUserByIdOrThrow(id);

        if (request.username() != null && !request.username().equals((user.getUsername()))) {
            user.setUsername(request.username());
        }

        if (request.userImgUrl() != null && !request.userImgUrl().equals((user.getUserImgUrl()))) {
            user.setUserImgUrl(request.userImgUrl());
        }

        CustomUser updatedUser = customUserRepository.save(user);

        return CustomUserMapper.toDto(updatedUser);
    }

    public CustomUserResponse updateMyEmail(Long id, CustomUserUpdateEmailRequest request) {

        CustomUser user = getActiveUserByIdOrThrow(id);

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid Password");
        }

        if (customUserRepository.existsByEmail(request.newEmail())) {
            throw new EntityAlreadyExistsException("E-mail " + request.newEmail() + " is already registered");
        }

        user.setEmail(request.newEmail());

        CustomUser updatedUser = customUserRepository.save(user);

        return CustomUserMapper.toDto(updatedUser);
    }

    public void updateMyPassword(Long id, CustomUserUpdatePasswordRequest request) {

        CustomUser user = getActiveUserByIdOrThrow(id);

        if(!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException(("Current password is incorrect"));
        }

        if(!request.newPassword().equals(request.confirmPassword())) {
            throw new InvalidCredentialsException("New Password and confirmation do not match");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        customUserRepository.save(user);
    }

    public void deactivateMyProfile(Long id) {

        CustomUser user = getActiveUserByIdOrThrow(id);

        user.setIsActive(false);

        customUserRepository.save(user);
    }

    @Transactional(readOnly = true)
    private CustomUser getActiveUserByIdOrThrow(Long id) {
        return customUserRepository.findByIdAndIsActive(id, true).orElseThrow(() -> new EntityNotFoundException("Active user with id: " + id + " not found"));
    }
}
