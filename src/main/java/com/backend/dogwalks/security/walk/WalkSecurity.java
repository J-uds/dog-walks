package com.backend.dogwalks.security.walk;

import com.backend.dogwalks.exception.custom_exception.EntityNotFoundException;
import com.backend.dogwalks.security.user.CustomUserDetails;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.walk.entity.Walk;
import com.backend.dogwalks.walk.repository.WalkRepository;
import org.springframework.stereotype.Component;

@Component("walkSecurity")
public class WalkSecurity {

    private final WalkRepository walkRepository;

    public WalkSecurity(WalkRepository walkRepository) {
        this.walkRepository = walkRepository;
    }

    public boolean canAccessWalk(Long walkId, CustomUserDetails userDetails) {
        Walk walk = walkRepository.findById(walkId).orElseThrow(() -> new EntityNotFoundException("Walk with id " + walkId + " not found"));

        CustomUser user = userDetails.getUser();

        return walk.getUser().equals(user) || userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
