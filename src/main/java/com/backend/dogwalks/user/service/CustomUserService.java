package com.backend.dogwalks.user.service;

import com.backend.dogwalks.exception.custom_exception.UsernameNotFoundException;
import com.backend.dogwalks.security.CustomUserDetails;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService {

}
