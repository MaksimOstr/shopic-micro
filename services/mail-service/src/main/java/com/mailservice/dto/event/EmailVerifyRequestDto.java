package com.mailservice.dto.event;

public record EmailVerifyRequestDto(
        String code,
        String email
) {
}
