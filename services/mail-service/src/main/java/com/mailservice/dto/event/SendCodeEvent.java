package com.mailservice.dto.event;

public record SendCodeEvent(
        String code,
        String email
) {
}
