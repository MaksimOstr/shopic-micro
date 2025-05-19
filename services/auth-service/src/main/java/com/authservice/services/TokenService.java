package com.authservice.services;

import com.authservice.dto.TokenPairDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenCreationService refreshTokenCreationService;
    private final JwtTokenService jwtTokenService;

    public TokenPairDto getTokenPair(long userId, Set<String> userRoles, String deviceId) {
        String accessToken = jwtTokenService.getJwsToken(userRoles, userId);
        String refreshToken = refreshTokenCreationService.create(userId, deviceId);

        return new TokenPairDto(accessToken, refreshToken);
    }
}
