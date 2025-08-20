package com.backend.dogwalks.user.service;

import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService {

    private final CustomUserRepository customUserRepository;

    public CustomUserService(CustomUserRepository customUserRepository) {
        this.customUserRepository = customUserRepository;
    }


}
