package com.userservice.dto.event;

public record EmailVerifyRequestDto(
        long userId,
        String email
) {}
