package com.reviewservice.dto.request;

import com.reviewservice.entity.ReportStatus;

public record UserReportParams(
        ReportStatus status
) {}
