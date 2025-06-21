package com.userservice.dto.event;

public record EmailVerifyRequestDto(
        String code,
        String email
) {}
