package com.reviewservice.dto.request;

import com.reviewservice.entity.ReportStatus;

public record AdminReportParams(
        Long userId,
        Long reviewId,
        Long commentId,
        ReportStatus status,
        String reportType
) {}
