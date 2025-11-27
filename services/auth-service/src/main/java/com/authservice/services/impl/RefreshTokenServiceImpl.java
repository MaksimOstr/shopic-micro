package com.authservice.services.impl;

import com.authservice.config.properties.RefreshTokenProperties;
import com.authservice.entity.RefreshToken;
import com.authservice.entity.User;
import com.authservice.exception.ApiException;
import com.authservice.repositories.RefreshTokenRepository;
import com.authservice.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final RefreshTokenProperties properties;


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
            throw new ApiException("Session is expired", HttpStatus.UNAUTHORIZED);
        }

        refreshTokenRepository.delete(token);

        return token;
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteRefreshTokenByToken(hashedToken(token));
    }


    private RefreshToken findToken(String token) {
        return refreshTokenRepository.findByToken(hashedToken(token))
                .orElseThrow(() -> new ApiException("Refresh token not found",  HttpStatus.NOT_FOUND));
    }

    private String hashedToken(String token) {
        return createHmac(token, properties.getSecret());
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private Instant getExpireTime() {
        return Instant.now().plusSeconds(properties.getExpiresAt());
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void deleteExpiredTokens() {
        log.info("Deleting expired refresh tokens");
        refreshTokenRepository.deleteExpiredTokens();
    }
}
