package com.backend.dogwalks.walk.entity;

import com.backend.dogwalks.user.entity.CustomUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

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

    public Walk(String title, String location, Integer duration, String description, String walkImgUrl, CustomUser user) {
        this.title = title;
        this.location = location;
        this.duration = duration;
        this.description = description;
        this.walkImgUrl = walkImgUrl;
        this.user = user;
    }
}
