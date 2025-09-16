package com.backend.dogwalks.user.entity;

import com.backend.dogwalks.user.enums.Role;
import com.backend.dogwalks.walk.entity.Walk;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 300)
    private String password;

    @Column(name = "img")
    private String userImgUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Walk> walks = new ArrayList<>();

    public CustomUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public CustomUser(String username, String email, String userImgUrl, Role role, Boolean isActive) {
        this.username = username;
        this.email = email;
        this.userImgUrl = userImgUrl;
        this.role = role;
        this.isActive = isActive;
    }
}

