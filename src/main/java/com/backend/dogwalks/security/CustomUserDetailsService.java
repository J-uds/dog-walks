package com.backend.dogwalks.security;

import com.backend.dogwalks.exception.custom_exception.UsernameNotFoundException;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final CustomUserRepository customUserRepository;

    public CustomUserDetailsService(CustomUserRepository customUserRepository) {
        this.customUserRepository = customUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return customUserRepository.findUserByEmail(email)
                .map(user -> new CustomUserDetails(user))
                .orElseThrow(() -> new UsernameNotFoundException("User with e-mail: " + email + ", not found"));
    }
}
