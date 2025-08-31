package com.backend.dogwalks.utils;

import com.backend.dogwalks.security.user.CustomUserDetails;
import com.backend.dogwalks.security.user.jwt.JwtUtil;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

@Component
public class IntegrationTestUtils {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserRepository customUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    public String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    public <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }

    public CustomUser createUser(String email, String rawPassword, Role role) {
        CustomUser user = new CustomUser();
        user.setEmail(email);
        user.setPassword(rawPassword);
        user.setRole(role);

        return customUserRepository.save(user);
    }

    public String generateToken(CustomUser user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);

        return jwtUtil.generateToken(userDetails);
    }
}
