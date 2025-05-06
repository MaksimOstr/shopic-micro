package com.authservice.services;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenService jwtTokenService;
}
