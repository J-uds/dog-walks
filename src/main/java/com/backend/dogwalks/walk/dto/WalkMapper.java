package com.backend.dogwalks.walk.dto;

import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.walk.entity.Walk;

public class WalkMapper {

    public static Walk toEntity(WalkRequest request, CustomUser user) {
        return new Walk(
                request.title(),
                request.location(),
                request.duration(),
                request.description(),
                request.walkImgUrl(),
                request.isActive(),
                user
        );
    }

    public static WalkResponse toDto(Walk walk) {
        return new WalkResponse(
                walk.getId(),
                walk.getTitle(),
                walk.getCreatedAt(),
                walk.getLocation(),
                walk.getDuration(),
                walk.getDescription(),
                walk.getWalkImgUrl(),
                walk.getIsActive(),
                walk.getUser().getUsername()
        );
    }

    public static WalkSummaryResponse toSummaryDto(Walk walk) {
        return new WalkSummaryResponse(
                walk.getId(),
                walk.getTitle(),
                walk.getLocation(),
                walk.getDuration(),
                walk.getWalkImgUrl(),
                walk.getCreatedAt()
        );
    }

    public static WalkDetailResponse toDetailDto(Walk walk) {
        return new WalkDetailResponse(
                walk.getId(),
                walk.getTitle(),
                walk.getLocation(),
                walk.getDuration(),
                walk.getDescription(),
                walk.getWalkImgUrl(),
                walk.getUser().getUsername(),
                walk.getCreatedAt()
        );
    }

    public static WalkAdminDtoResponse toDtoAdmin(Walk walk) {
        return new WalkAdminDtoResponse(
                walk.getId(),
                walk.getTitle(),
                walk.getLocation()
        );
    }

    public static void updateFromWalkRequest(Walk walk, WalkRequest request) {
        if (request.title() != null) {
            walk.setTitle(request.title());
        }
        if (request.location() != null) {
            walk.setLocation(request.location());
        }
        if (request.duration() != null) {
            walk.setDuration(request.duration());
        }
        if (request.description() != null) {
            walk.setDescription(request.description());
        }
        if (request.walkImgUrl() != null) {
            walk.setWalkImgUrl(request.walkImgUrl());
        }
        if (request.isActive() != null) {
            walk.setIsActive(request.isActive());
        }
        if (walk.getUser() != null) {
            walk.setUser(walk.getUser());
        }
    }
}
