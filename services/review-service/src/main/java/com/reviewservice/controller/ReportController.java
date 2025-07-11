package com.reviewservice.controller;

import com.reviewservice.config.security.model.CustomPrincipal;
import com.reviewservice.dto.ReportDto;
import com.reviewservice.dto.ReportStatusDto;
import com.reviewservice.dto.request.CreateCommentReport;
import com.reviewservice.dto.request.CreateReviewReport;
import com.reviewservice.dto.request.UserReportParams;
import com.reviewservice.entity.Report;
import com.reviewservice.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getReport(
            @PathVariable long id,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        ReportDto report = reportService.getReportDto(id, principal.getId());

        return ResponseEntity.ok(report);
    }


    @GetMapping
    public ResponseEntity<Page<ReportStatusDto>> getReports(
            @RequestBody UserReportParams body,
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportStatusDto> reports = reportService.getReports(body, principal.getId(), pageable);

        return ResponseEntity.ok(reports);
    }


}
