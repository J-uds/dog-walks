package com.backend.dogwalks.security;

import com.backend.dogwalks.user.entity.CustomUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class  CustomUserDetail implements UserDetails {
    private final CustomUser user;

    public CustomUserDetail(CustomUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    //se loguea con email
    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
