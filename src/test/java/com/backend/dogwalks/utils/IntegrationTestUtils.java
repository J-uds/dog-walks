package com.backend.dogwalks.utils;

import com.backend.dogwalks.security.user.CustomUserDetails;
import com.backend.dogwalks.security.user.jwt.JwtUtil;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.user.repository.CustomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestConfiguration
public class IntegrationTestUtils {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserRepository customUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    public String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }

    public CustomUser createUser(String username, String email, String rawPassword, Role role) {
        CustomUser user = new CustomUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setIsActive(true);

        return customUserRepository.save(user);
    }

    public String generateToken(CustomUser user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);

        return jwtUtil.generateToken(userDetails);
    }
}
