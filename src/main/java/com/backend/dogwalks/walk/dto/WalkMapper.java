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
                walk.getIsActive(),
                walk.getUser().getUsername(),
                walk.getUser().getEmail()
        );
    }

    public static WalkAdminResponse toDtoAdmin(Walk walk) {
        return new WalkAdminResponse(
                walk.getId(),
                walk.getTitle(),
                walk.getLocation()
        );
    }

  /*  public static void updateFromWalkRequest(Walk walk, WalkRequest request, CustomUser user) {
        if (request.title() != null) {
            walk.setTitle(request.title());
        }
        if(request.location() != null) {
            walk.setLocation(request.location());
        }
        if(walk.getUser() != null) {
            walk.setUser(user);
        }
    }*/
}
