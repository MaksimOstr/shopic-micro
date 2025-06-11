package com.authservice.services;

import com.authservice.entity.RefreshToken;
import com.authservice.exceptions.TokenValidationException;
import com.authservice.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.authservice.utils.CryptoUtils.createHmac;


@Service
@RequiredArgsConstructor
public class RefreshTokenValidationService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${REFRESH_TOKEN_SECRET}")
    private String refreshSecret;

    @Transactional
    public RefreshToken validate(String refreshToken) {
        RefreshToken token = findToken(refreshToken);

        if(token.getExpiresAt().isBefore(Instant.now())) {
            deleteTokenById(token.getId());
            throw new TokenValidationException("Session is expired");
        }

        return token;
    }

    private void deleteTokenById(long id) {
        refreshTokenRepository.deleteById(id);
    }

    private RefreshToken findToken(String token) {
        return refreshTokenRepository.findByToken(createHmac(token, refreshSecret))
                .orElseThrow(() -> new TokenValidationException("Refresh token not found"));
    }
}
