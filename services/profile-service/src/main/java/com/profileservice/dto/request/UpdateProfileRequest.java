package com.profileservice.dto.request;

public record UpdateProfileRequest(
        String lastName,
        String firstName
) {
}
