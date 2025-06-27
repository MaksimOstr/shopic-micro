package com.paymentservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChargeRequest(
        @NotNull
        long amount,

        String description,

        @NotBlank
        String paymentMethodId
) {
}
