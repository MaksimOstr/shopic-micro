package com.authservice.dto.request;

public record UpdateProfileRequest(
        String lastName,
        String firstName
) {
}
