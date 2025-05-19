package com.authservice.services;

import com.authservice.dto.TokenPairDto;
import com.authservice.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.authservice.utils.AuthUtils.mapUserRoles;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenCreationService refreshTokenCreationService;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenValidationService refreshTokenService;

    @Transactional
    public TokenPairDto refreshTokens(String refreshToken, String deviceId) {
        RefreshToken validatedRefreshToken = refreshTokenService.validate(refreshToken, deviceId);
        long userId = validatedRefreshToken.getUserId();
        String newRefreshToken = refreshTokenCreationService.updateRefreshToken(validatedRefreshToken);
        return new TokenPairDto(getAccessToken(userId, mapUserRoles(user)), newRefreshToken);
    }

    public TokenPairDto getTokenPair(long userId, Set<String> userRoles, String deviceId) {
        String accessToken = getAccessToken(userId, userRoles);
        String refreshToken = refreshTokenCreationService.create(userId, deviceId);

        return new TokenPairDto(accessToken, refreshToken);
    }

    private String getAccessToken(long userId, Set<String> roleNames) {
        return jwtTokenService.getJwsToken(roleNames, userId);
    }
}
