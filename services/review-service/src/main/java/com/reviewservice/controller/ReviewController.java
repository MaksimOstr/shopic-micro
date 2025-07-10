package com.reviewservice.controller;

import com.reviewservice.config.security.model.CustomPrincipal;
import com.reviewservice.dto.ReviewDto;
import com.reviewservice.dto.request.CreateReviewRequest;
import com.reviewservice.dto.request.UpdateReviewRequest;
import com.reviewservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


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

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<ReviewDto>> getReviews(
            @RequestParam(required = false) Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDto> reviews = reviewService.getReviewsByProductId(productId, pageable);

        return ResponseEntity.ok(reviews);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateReview(
            @RequestBody UpdateReviewRequest body,
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long id
    ) {
        reviewService.updateReview(body, principal.getId(), id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        reviewService.deleteReview(id, principal.getId());

        return ResponseEntity.ok().build();
    }
}
