package com.banservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record BanRequest(
        @NotNull
        @Future
        Instant banTo,

        @NotNull
        Long userId,

        @NotBlank
        String reason
) {}
