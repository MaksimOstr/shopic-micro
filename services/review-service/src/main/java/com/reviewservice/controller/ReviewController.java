package com.reviewservice.controller;

import com.reviewservice.config.security.model.CustomPrincipal;
import com.reviewservice.dto.ReviewCommentDto;
import com.reviewservice.dto.ReviewDto;
import com.reviewservice.dto.request.CreateReviewCommentRequest;
import com.reviewservice.dto.request.CreateReviewRequest;
import com.reviewservice.service.ReviewCommentService;
import com.reviewservice.service.ReviewService;
import jakarta.validation.Valid;
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
    private final ReviewCommentService reviewCommentService;

    @PostMapping
    public ResponseEntity<Void> createReview(
            @RequestBody CreateReviewRequest body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        reviewService.createReview(body, principal.getId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Void> createReviewComment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid CreateReviewCommentRequest body
    ) {
        reviewCommentService.createReviewComment(body, principal.getId(), id);

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

    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<ReviewCommentDto>> getReviewComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable long id
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewCommentDto> reviews = reviewCommentService.getReviewComments(id, pageable);

        return ResponseEntity.ok(reviews);
    }
}
