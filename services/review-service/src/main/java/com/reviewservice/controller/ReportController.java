package com.reviewservice.controller;

import com.reviewservice.config.security.model.CustomPrincipal;
import com.reviewservice.dto.ReportDto;
import com.reviewservice.dto.ReportStatusDto;
import com.reviewservice.dto.request.CreateCommentReport;
import com.reviewservice.dto.request.CreateReviewReport;
import com.reviewservice.dto.request.UserReportParams;
import com.reviewservice.entity.Report;
import com.reviewservice.service.CommentReportService;
import com.reviewservice.service.ReportService;
import com.reviewservice.service.ReviewReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class ReportController {
    private final ReportService reportService;
    private final ReviewReportService reviewReportService;
    private final CommentReportService commentReportService;


    @PostMapping("/comment")
    public ResponseEntity<Void> reportComment(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid CreateCommentReport body
    ) {
        commentReportService.reportComment(body, principal.getId());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/review")
    public ResponseEntity<Void> reportReview(
            @RequestBody @Valid CreateReviewReport body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        reviewReportService.reportReview(body, principal.getId());

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReportStatusDto>> getReports(
            @ModelAttribute UserReportParams body,
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportStatusDto> reports = reportService.getReports(body, principal.getId(), pageable);

        return ResponseEntity.ok(reports);
    }


}
