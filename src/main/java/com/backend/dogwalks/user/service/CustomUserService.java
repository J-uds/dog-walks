package com.backend.dogwalks.user.service;

import com.backend.dogwalks.user.repository.CustomUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService implements UserDetailsService {
    private final CustomUserRepository customUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;



}
