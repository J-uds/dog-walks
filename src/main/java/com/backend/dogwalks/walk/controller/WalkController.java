package com.backend.dogwalks.walk.controller;

import com.backend.dogwalks.security.user.CustomUserDetails;
import com.backend.dogwalks.user.entity.CustomUser;
import com.backend.dogwalks.walk.dto.WalkDetailResponse;
import com.backend.dogwalks.walk.dto.WalkRequest;
import com.backend.dogwalks.walk.dto.WalkResponse;
import com.backend.dogwalks.walk.dto.WalkSummaryResponse;
import com.backend.dogwalks.walk.service.WalkService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/walks")
public class WalkController {

    private final WalkService walkService;

    public WalkController(WalkService walkService) {
        this.walkService = walkService;
    }

    @GetMapping("/public")
    public ResponseEntity<Page<WalkSummaryResponse>> getAllWalkSummaryPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Page<WalkSummaryResponse> walks = walkService.getAllWalksSummary(page, size, sortBy, sortDir);

        return new ResponseEntity<>(walks, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("@walkSecurity.canAccessWalk(#id, authentication.principal)")
    public ResponseEntity<Page<WalkResponse>> getAllWalkPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Page<WalkResponse> walks = walkService.getAllWalksPaginated(page, size, sortBy, sortDir);

        return new ResponseEntity<>(walks, HttpStatus.OK);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<WalkDetailResponse> getWalkDetailById(@PathVariable Long id) {

        WalkDetailResponse response = walkService.getWalkDetailById(id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@walkSecurity.canAccessWalk(#id, authentication.principal)")
    public ResponseEntity<WalkResponse> getWalkById(@PathVariable Long id) {

        WalkResponse response = walkService.getWalkById(id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WalkResponse> addWalk(@Valid @RequestBody WalkRequest request, Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        CustomUser user = userDetails.getUser();
        WalkResponse response = walkService.addWalk(request, user);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@walkSecurity.canAccessWalk(#id, authentication.principal)")
    public ResponseEntity<WalkResponse> updateWalk(@PathVariable Long id, @Valid @RequestBody WalkRequest request) {

        WalkResponse response = walkService.updateWalk(id, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@walkSecurity.canAccessWalk(#id, authentication.principal)")
    public ResponseEntity<Void> deleteWalk(@PathVariable Long id) {

        walkService.deleteWalk(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
