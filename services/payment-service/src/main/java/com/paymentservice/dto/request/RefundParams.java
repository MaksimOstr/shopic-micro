package com.paymentservice.dto.request;

import com.paymentservice.entity.RefundReason;
import com.paymentservice.entity.RefundStatus;

import java.math.BigDecimal;

public record RefundParams(
        RefundStatus status,
        BigDecimal amountFrom,
        BigDecimal amountTo,
        RefundReason reason,
        String stripeRefundId,
        Long paymentId
) {}
