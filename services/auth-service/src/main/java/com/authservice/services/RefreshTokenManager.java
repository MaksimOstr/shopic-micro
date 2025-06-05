package com.authservice.services;

import com.authservice.entity.RefreshToken;
import com.authservice.exceptions.EntityAlreadyExistsException;
import com.authservice.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.authservice.utils.CryptoUtils.createHmac;


@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenManager {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${REFRESH_TOKEN_SECRET}")
    private String refreshSecret;

    @Value("${REFRESH_TOKEN_TTL:3600}")
    private int refreshTokenTtl;


    @Transactional
    public String create(long userId, String deviceId) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserIdAndDeviceId(userId, deviceId);

        if (optionalRefreshToken.isPresent()) {
            return updateRefreshToken(optionalRefreshToken.get());
        } else {
            return createNewRefreshToken(userId, deviceId);
        }
    }


    public String updateRefreshToken(RefreshToken token) {
        String newToken = generateToken();
        String hashed = hashedToken(newToken);

        token.setExpiresAt(getExpireTime());
        token.setToken(hashed);

        saveRefreshToken(token);

        return newToken;
    }

    public void deleteRefreshToken(String token, String deviceId) {
      int deleted = refreshTokenRepository.deleteRefreshTokenByTokenAndDeviceId(hashedToken(token), deviceId);

      if (deleted == 0) {
          log.info("Refresh token was not deleted");
      }
    }


    private String createNewRefreshToken(long userId, String deviceId) {
        String newToken = generateToken();
        RefreshToken refreshToken = new RefreshToken(
                hashedToken(newToken),
                getExpireTime(),
                userId,
                deviceId
        );

        saveRefreshToken(refreshToken);

        return newToken;
    }


    private String hashedToken(String token) {
        return createHmac(token, refreshSecret);
    }


    private void saveRefreshToken(RefreshToken refreshToken) {
        try {
            refreshTokenRepository.save(refreshToken);
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistsException("Refresh token was not created");
        }
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
