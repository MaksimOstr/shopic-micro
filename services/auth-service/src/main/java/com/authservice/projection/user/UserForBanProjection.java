package com.authservice.projection.user;

public record UserForBanProjection(
        boolean isVerified,
        String email
) {
}
