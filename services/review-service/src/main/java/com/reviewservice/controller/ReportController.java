package com.reviewservice.controller;

import com.reviewservice.config.security.model.CustomPrincipal;
import com.reviewservice.dto.request.CreateCommentReport;
import com.reviewservice.dto.request.CreateReviewReport;
import com.reviewservice.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class ReportController {
    private final ReportService reportService;


    @PostMapping("/comment")
    public ResponseEntity<Void> reportComment(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid CreateCommentReport body
    ) {
        reportService.reportComment(body, principal.getId());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/review")
    public ResponseEntity<Void> reportReview(
            @RequestBody @Valid CreateReviewReport body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        reportService.reportReview(body, principal.getId());

        return ResponseEntity.ok().build();
    }
}
