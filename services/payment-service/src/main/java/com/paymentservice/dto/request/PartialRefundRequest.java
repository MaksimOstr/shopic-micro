package com.paymentservice.dto.request;

import com.paymentservice.entity.RefundReason;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PartialRefundRequest(
        @NotNull
        RefundReason reason,

        @NotNull
        @DecimalMin("1")
        BigDecimal amount
) {
}
