package com.reviewservice.controller;

import com.reviewservice.dto.ReportDto;
import com.reviewservice.dto.request.AdminReportParams;
import com.reviewservice.dto.request.ReportStatusUpdateRequest;
import com.reviewservice.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final ReportService reportService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeReportStatus(
            @PathVariable int id,
            @RequestBody @Valid ReportStatusUpdateRequest body
    ) {
        reportService.changeReportStatus(id, body.status());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getReport(
            @PathVariable int id
    ) {
        ReportDto report = reportService.getReportDto(id);

        return ResponseEntity.ok(report);
    }


    @GetMapping
    public ResponseEntity<Page<ReportDto>> getReports(
            @RequestBody AdminReportParams body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportDto> reports = reportService.getReports(body, pageable);

        return ResponseEntity.ok(reports);
    }
}
