package com.authservice.unit.service;

import com.authservice.config.properties.RefreshTokenProperties;
import com.authservice.entity.RefreshToken;
import com.authservice.entity.User;
import com.authservice.exception.ApiException;
import com.authservice.repositories.RefreshTokenRepository;
import com.authservice.services.impl.RefreshTokenServiceImpl;
import com.authservice.utils.CryptoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenServiceImpl refreshTokenService;

    private RefreshTokenProperties properties;
    private User user;

    @BeforeEach
    void setUp() {
        properties = new RefreshTokenProperties();
        properties.setSecret("secret");
        properties.setExpiresAt(300);
        refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository, properties);
        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .build();
    }

    @Test
    void create_shouldHashTokenAndPersistForUser() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        Instant before = Instant.now();

        String rawToken = refreshTokenService.create(user);

        verify(refreshTokenRepository).deleteAllByUser(user);
        verify(refreshTokenRepository).save(captor.capture());

        RefreshToken saved = captor.getValue();
        String expectedHash = CryptoUtils.createHmac(rawToken, properties.getSecret());
        assertEquals(expectedHash, saved.getToken());
        assertEquals(user, saved.getUser());
        assertTrue(saved.getExpiresAt().isAfter(before.plusSeconds(298)));
    }

    @Test
    void validate_shouldReturnTokenWhenExistsAndNotExpired() {
        String rawToken = "rawToken";
        String hashed = CryptoUtils.createHmac(rawToken, properties.getSecret());
        RefreshToken stored = RefreshToken.builder()
                .token(hashed)
                .user(user)
                .expiresAt(Instant.now().plusSeconds(10))
                .build();
        when(refreshTokenRepository.findByToken(hashed)).thenReturn(Optional.of(stored));

        RefreshToken result = refreshTokenService.validate(rawToken);

        verify(refreshTokenRepository).findByToken(hashed);
        assertEquals(stored, result);
    }

    @Test
    void validate_shouldThrowWhenTokenMissing() {
        String rawToken = "missing";
        String hashed = CryptoUtils.createHmac(rawToken, properties.getSecret());
        when(refreshTokenRepository.findByToken(hashed)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> refreshTokenService.validate(rawToken));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void validate_shouldThrowWhenTokenExpired() {
        String rawToken = "expired";
        String hashed = CryptoUtils.createHmac(rawToken, properties.getSecret());
        RefreshToken stored = RefreshToken.builder()
                .token(hashed)
                .expiresAt(Instant.now().minusSeconds(1))
                .user(user)
                .build();
        when(refreshTokenRepository.findByToken(hashed)).thenReturn(Optional.of(stored));

        ApiException exception = assertThrows(ApiException.class, () -> refreshTokenService.validate(rawToken));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void deleteRefreshToken_shouldHashAndDelete() {
        String rawToken = "raw";
        String hashed = CryptoUtils.createHmac(rawToken, properties.getSecret());

        refreshTokenService.deleteRefreshToken(rawToken);

        verify(refreshTokenRepository).deleteRefreshTokenByToken(hashed);
    }
}
