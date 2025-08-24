package com.backend.dogwalks.walk.entity;

import com.backend.dogwalks.user.entity.CustomUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "walks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Walk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    private String description;

    @Column(name = "img")
    private String walkImgUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private CustomUser user;
}
