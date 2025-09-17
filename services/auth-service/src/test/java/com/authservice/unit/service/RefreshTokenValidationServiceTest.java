package com.authservice.unit.service;

import com.authservice.entity.RefreshToken;
import com.authservice.exceptions.TokenValidationException;
import com.authservice.repositories.RefreshTokenRepository;
import com.authservice.services.RefreshTokenValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RefreshTokenValidationServiceTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenValidationService refreshTokenValidationService;

    private static final long REFRESH_TOKEN_ID = 1L;
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String DEVICE_ID = "device_id";
    private static final String REFRESH_TOKEN_SECRET = "testRefreshTokenSecret";


    private RefreshToken refreshToken;

    @BeforeEach
    public void setUp() {
        refreshToken = RefreshToken.builder()
                .id(REFRESH_TOKEN_ID)
                .token(REFRESH_TOKEN)
                .deviceId(DEVICE_ID)
                .build();

        ReflectionTestUtils.setField(refreshTokenValidationService, "refreshSecret", REFRESH_TOKEN_SECRET);
    }

    @Test
    public void testValidate_whenCalledWithValidToken_thenReturnToken() {
        refreshToken.setExpiresAt(Instant.now().plusSeconds(60));
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(refreshTokenRepository.findByTokenAndDeviceId(anyString(), anyString())).thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenValidationService.validate(REFRESH_TOKEN, DEVICE_ID);

        verify(refreshTokenRepository).findByTokenAndDeviceId(stringArgumentCaptor.capture(), eq(DEVICE_ID));
        verifyNoMoreInteractions(refreshTokenRepository);

        String usedToken = stringArgumentCaptor.getValue();

        assertNotEquals(REFRESH_TOKEN, usedToken);
        assertEquals(refreshToken, result);
    }

    @Test
    public void testValidate_whenCalledWithExpiredToken_thenThrowException() {
        refreshToken.setExpiresAt(Instant.now().minusSeconds(60));

        when(refreshTokenRepository.findByTokenAndDeviceId(anyString(), anyString())).thenReturn(Optional.of(refreshToken));

        assertThrows(TokenValidationException.class, () -> {
            refreshTokenValidationService.validate(REFRESH_TOKEN, DEVICE_ID);
        });

        verify(refreshTokenRepository).findByTokenAndDeviceId(anyString(), eq(DEVICE_ID));
        verify(refreshTokenRepository).deleteById(REFRESH_TOKEN_ID);
    }

    @Test
    public void testValidate_whenCalledWithNonExistentToken_thenThrowException() {
        refreshToken.setExpiresAt(Instant.now().minusSeconds(60));

        when(refreshTokenRepository.findByTokenAndDeviceId(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(TokenValidationException.class, () -> {
            refreshTokenValidationService.validate(REFRESH_TOKEN, DEVICE_ID);
        });

        verify(refreshTokenRepository).findByTokenAndDeviceId(anyString(), eq(DEVICE_ID));
        verifyNoMoreInteractions(refreshTokenRepository);
    }
}
