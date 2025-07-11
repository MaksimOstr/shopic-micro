package com.reviewservice.dto;

import com.reviewservice.entity.ReportStatus;

import java.time.Instant;

public class ReportStatusDto(
        long id,
        ReportStatus status,
        Instant createdAt
) {}
