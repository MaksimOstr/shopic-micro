package com.reviewservice.controller;

import com.reviewservice.dto.ReviewDto;
import com.reviewservice.dto.request.ReviewParams;
import com.reviewservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {
    private final ReviewService reviewService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id
    ) {
        reviewService.deleteReview(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReviewDto>> getReviews(
            @ModelAttribute ReviewParams body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDto> reviews = reviewService.getReviewsBySpec(pageable, body);

        return ResponseEntity.ok(reviews);
    }

}
