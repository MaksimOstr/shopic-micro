package com.reviewservice.dto.request;

import com.reviewservice.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;

public record ReportStatusUpdateRequest(
        @NotNull
        ReportStatus status
) {
}
