package com.reviewservice.dto;

import com.reviewservice.entity.ReportStatus;

public record ReportDto(
        long id,
        String description,
        ReportStatus status,
        Long reviewId,
        Long commentId,
        long reporter,
        String reportType
) {
}
