package com.authservice.projection.user;


import java.util.List;

public record UserForBanProjection(
        boolean isVerified,
        String email
) {
}
