package com.reviewservice.controller;

import com.reviewservice.dto.ReviewCommentDto;
import com.reviewservice.dto.request.ReviewCommentParams;
import com.reviewservice.service.ReviewCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/review-comments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewCommentController {

    private final ReviewCommentService reviewCommentService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReviewComment(
            @PathVariable long id
    ) {
        reviewCommentService.deleteReviewComment(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReviewCommentDto>> getReviewComments(
            @RequestBody ReviewCommentParams body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewCommentDto> comments = reviewCommentService.getReviewCommentsBySpec(body, pageable);

        return ResponseEntity.ok(comments);
    }
}
