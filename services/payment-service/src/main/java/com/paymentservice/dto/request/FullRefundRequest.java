package com.paymentservice.dto.request;

import com.paymentservice.entity.RefundReason;
import jakarta.validation.constraints.NotNull;

public record FullRefundRequest(
        @NotNull
        RefundReason reason
) {}
