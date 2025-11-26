package com.authservice.services;

import com.authservice.dto.TokenPairDto;
import com.authservice.entity.RefreshToken;
import com.authservice.entity.User;
import com.authservice.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;



@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenManager refreshTokenManager;
    private final JwtService jwtTokenService;
    private final RefreshTokenValidationService refreshTokenValidationService;
    private final RoleMapper roleMapper;


    @Transactional
    public TokenPairDto refreshTokens(String refreshToken) {
        RefreshToken validatedRefreshToken = refreshTokenValidationService.validate(refreshToken);
        User user = validatedRefreshToken.getUser();
        long userId = user.getId();
        List<String> roleNames = roleMapper.mapRolesToNames(user.getRoles());
        String newRefreshToken = refreshTokenManager.updateRefreshToken(validatedRefreshToken);

        return new TokenPairDto(getAccessToken(userId, roleNames), newRefreshToken);
    }

    public TokenPairDto getTokenPair(long userId, List<String> userRoles) {
        String accessToken = getAccessToken(userId, userRoles);
        String refreshToken = refreshTokenManager.create(userId);

        return new TokenPairDto(accessToken, refreshToken);
    }

    public void logout(String token) {
        refreshTokenManager.deleteRefreshToken(token);
    }

    private String getAccessToken(long userId, Collection<String> roleNames) {
        return jwtTokenService.generateToken(String.valueOf(userId), roleNames);
    }
}
