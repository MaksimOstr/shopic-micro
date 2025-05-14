package com.mailservice.dto.event;

public record EmailVerifyRequestDto(
        long userId,
        String email
) {
}
