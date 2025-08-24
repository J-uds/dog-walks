package com.backend.dogwalks.walk.entity;

import com.backend.dogwalks.user.entity.CustomUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(nullable = false, length = 100)
    private String location;

    private Integer duration;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "img")
    private String walkImgUrl;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private CustomUser user;
}
