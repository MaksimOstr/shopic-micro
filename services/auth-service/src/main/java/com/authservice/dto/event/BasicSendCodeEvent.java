package com.authservice.dto.event;

public record BasicSendCodeEvent(
        String code,
        String email
) {
}
