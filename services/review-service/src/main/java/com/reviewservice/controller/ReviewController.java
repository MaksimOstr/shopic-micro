package com.reviewservice.controller;

import com.reviewservice.config.security.model.CustomPrincipal;
import com.reviewservice.dto.request.CreateReviewRequest;
import com.reviewservice.entity.Review;
import com.reviewservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@PreAuthorize("hasRole('USER')")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Void> createReview(
            @RequestBody CreateReviewRequest body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        reviewService.createReview(body, principal.getId());

        return ResponseEntity.ok().build();
    }
}
