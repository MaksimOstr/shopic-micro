package com.reviewservice.controller;

import com.reviewservice.config.security.model.CustomPrincipal;
import com.reviewservice.dto.ReviewCommentDto;
import com.reviewservice.dto.request.CreateReviewCommentRequest;
import com.reviewservice.dto.request.UpdateReviewCommentRequest;
import com.reviewservice.service.ReviewCommentService;
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
@RequestMapping("/review-comments")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class ReviewCommentController {
    private final ReviewCommentService reviewCommentService;

    @GetMapping
    public ResponseEntity<Page<ReviewCommentDto>> getReviewComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam long reviewId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewCommentDto> reviews = reviewCommentService.getReviewComments(reviewId, pageable);

        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<Void> createReviewComment(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid CreateReviewCommentRequest body
    ) {
        reviewCommentService.createReviewComment(body, principal.getId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateReviewComment(
            @RequestBody @Valid UpdateReviewCommentRequest body,
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable long id
    ) {
        reviewCommentService.updateReviewComment(body, principal.getId(), id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReviewComment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        reviewCommentService.deleteReviewComment(id, principal.getId());

        return ResponseEntity.ok().build();
    }
}
