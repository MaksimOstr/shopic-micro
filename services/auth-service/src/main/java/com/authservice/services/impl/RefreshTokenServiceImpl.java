package com.authservice.services.impl;

import com.authservice.entity.RefreshToken;
import com.authservice.entity.User;
import com.authservice.exceptions.TokenValidationException;
import com.authservice.repositories.RefreshTokenRepository;
import com.authservice.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static com.authservice.utils.CryptoUtils.createHmac;


@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${REFRESH_TOKEN_SECRET}")
    private String refreshSecret;

    @Value("${REFRESH_TOKEN_TTL:3600}")
    private int refreshTokenTtl;


    @Transactional
    public String create(User user) {
        refreshTokenRepository.deleteAllByUser(user);

        String rawToken = generateToken();
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(hashedToken(rawToken))
                .user(user)
                .expiresAt(getExpireTime())
                .build();

        refreshTokenRepository.save(newRefreshToken);
        return rawToken;
    }

    @Transactional
    public RefreshToken validate(String refreshToken) {
        RefreshToken token = findToken(refreshToken);

        if(token.getExpiresAt().isBefore(Instant.now())) {
            throw new TokenValidationException("Session is expired");
        }

        refreshTokenRepository.delete(token);

        return token;
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteRefreshTokenByToken(hashedToken(token));
    }


    private RefreshToken findToken(String token) {
        return refreshTokenRepository.findByToken(createHmac(token, refreshSecret))
                .orElseThrow(() -> new TokenValidationException("Refresh token not found"));
    }

    private String hashedToken(String token) {
        return createHmac(token, refreshSecret);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private Instant getExpireTime() {
        return Instant.now().plusSeconds(refreshTokenTtl);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void deleteExpiredTokens() {
        log.info("Deleting expired refresh tokens");
        refreshTokenRepository.deleteExpiredTokens();
    }
}
