package com.paymentservice.dto.request;

import com.paymentservice.entity.RefundReason;

import java.math.BigDecimal;

public record RefundRequest(
        BigDecimal amount,
        RefundReason reason
) {}
