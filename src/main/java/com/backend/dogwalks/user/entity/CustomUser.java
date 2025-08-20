package com.backend.dogwalks.user.entity;

import com.backend.dogwalks.user.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
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

    /*@Column(nullable = false)
    private Boolean isActive;*/

    public CustomUser(String username, String email, String password, String userImgUrl) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userImgUrl = userImgUrl;
    }

    public CustomUser(String username, String email, String password, String userImgUrl, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userImgUrl = userImgUrl;
        this.role = role;
    }
}

