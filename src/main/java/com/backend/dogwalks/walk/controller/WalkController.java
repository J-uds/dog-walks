package com.backend.dogwalks.walk.controller;

import com.backend.dogwalks.walk.dto.WalkResponse;
import com.backend.dogwalks.walk.service.WalkService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/walks")
public class WalkController {

    private final WalkService walkService;

    public WalkController(WalkService walkService) {
        this.walkService = walkService;
    }

    @GetMapping
    public ResponseEntity<Page<WalkResponse>> getAllWalkPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Page<WalkResponse> walks = walkService.getAllWalksPaginated(page, size, sortBy, sortDir);

        return new ResponseEntity<>(walks, HttpStatus.OK);
    }
}
