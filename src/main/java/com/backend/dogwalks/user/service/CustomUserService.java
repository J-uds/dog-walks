package com.backend.dogwalks.user.service;

import com.backend.dogwalks.exception.custom_exception.UsernameNotFoundException;
import com.backend.dogwalks.security.CustomUserDetails;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService implements UserDetailsService {
    private final CustomUserRepository customUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CustomUserService(CustomUserRepository customUserRepository, BCryptPasswordEncoder passwordEncoder) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customUserRepository.findByUsername(username)
                .map(user -> new CustomUserDetails(user))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }


}
