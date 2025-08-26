package com.backend.dogwalks.user.controller;

import com.backend.dogwalks.security.user.CustomUserDetails;
import com.backend.dogwalks.user.dto.user.CustomUserResponse;
import com.backend.dogwalks.user.dto.user.CustomUserUpdateEmailRequest;
import com.backend.dogwalks.user.dto.user.CustomUserUpdatePasswordRequest;
import com.backend.dogwalks.user.dto.user.CustomUserUpdateRequest;
import com.backend.dogwalks.user.service.CustomUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class CustomUserController {

    private final CustomUserService customUserService;

    public CustomUserController(CustomUserService customUserService) {
        this.customUserService = customUserService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CustomUserResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long id = userDetails.getId();
        CustomUserResponse userResponse = customUserService.getMyProfile(id);

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CustomUserResponse> updateMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CustomUserUpdateRequest request) {

        Long id = userDetails.getId();
        CustomUserResponse updatedUser = customUserService.updateMyProfile(id, request);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PutMapping("/profile/email")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CustomUserResponse> updateMyEmail(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CustomUserUpdateEmailRequest request) {

        Long id = userDetails.getId();
        CustomUserResponse updatedUser = customUserService.updateMyEmail(id, request);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PutMapping("/profile/password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateMyPassword(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CustomUserUpdatePasswordRequest request) {

        Long id = userDetails.getId();
        customUserService.updateMyPassword(id, request);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/profile/deactivate")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {

        Long id = userDetails.getId();
        customUserService.deactivateMyProfile(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
