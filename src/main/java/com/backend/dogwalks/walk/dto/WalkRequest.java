package com.backend.dogwalks.walk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record WalkRequest(

        @NotBlank(message = "Title is needed")
        @Size(min= 2, max = 100, message = "Title must contain between 2 and 100 characters")
        String title,

        @Size(min= 2, max = 100, message = "Title must contain between 2 and 100 characters")
        String location,
        Integer duration,
        String description,
        String walkImgUrl
) {
}
