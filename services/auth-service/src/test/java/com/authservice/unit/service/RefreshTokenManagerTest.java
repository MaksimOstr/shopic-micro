package com.authservice.unit.service;

import com.authservice.entity.RefreshToken;
import com.authservice.entity.User;
import com.authservice.exception.AlreadyExistsException;
import com.authservice.repositories.RefreshTokenRepository;
import com.authservice.services.impl.RefreshTokenServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RefreshTokenManagerTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenManager;

    private static final long USER_ID = 1L;
    private static final String DEVICE_ID = "testDeviceId";
    private static final String REFRESH_TOKEN = "testRefreshToken";
    private static final String REFRESH_TOKEN_SECRET = "testRefreshTokenSecret";


    private RefreshToken refreshToken;
    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(USER_ID)
                .build();

        refreshToken = RefreshToken.builder()
                .token(REFRESH_TOKEN)
                .user(user)
                .expiresAt(Instant.now())
                .build();

        ReflectionTestUtils.setField(refreshTokenManager, "refreshSecret", REFRESH_TOKEN_SECRET);
        ReflectionTestUtils.setField(refreshTokenManager, "refreshTokenTtl", 3600);
    }


    @Test
    public void testCreate_whenCalledWithNonExistingRefreshToken_thenCreateNewToken() {
        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);

        when(refreshTokenRepository.findByUserIdAndDeviceId(anyLong(), anyString())).thenReturn(Optional.empty());
        when(entityManager.getReference(any(), anyLong())).thenReturn(user);

        String result = refreshTokenManager.create(USER_ID, DEVICE_ID);

        verify(refreshTokenRepository).findByUserIdAndDeviceId(USER_ID, DEVICE_ID);
        verify(entityManager).getReference(User.class, USER_ID);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());

        RefreshToken savedRefreshToken = refreshTokenCaptor.getValue();

        assertNotNull(savedRefreshToken.getToken());
        assertNotEquals(REFRESH_TOKEN, savedRefreshToken.getToken());
        assertEquals(user, savedRefreshToken.getUser());
        assertEquals(DEVICE_ID, savedRefreshToken.getDeviceId());
        assertNotNull(savedRefreshToken.getExpiresAt());
        assertNotEquals(result, savedRefreshToken.getToken());
        assertTrue(savedRefreshToken.getExpiresAt().isAfter(Instant.now()));
        assertNotEquals(REFRESH_TOKEN, result);
    }

    @Test
    public void testCreate_whenCalledWithExistingRefreshToken_thenReturnUpdatedToken() {
        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);

        when(refreshTokenRepository.findByUserIdAndDeviceId(anyLong(), anyString())).thenReturn(Optional.of(refreshToken));

        String result =  refreshTokenManager.create(USER_ID, DEVICE_ID);

        verify(refreshTokenRepository).findByUserIdAndDeviceId(USER_ID, DEVICE_ID);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
        verifyNoInteractions(entityManager);

        RefreshToken savedRefreshToken = refreshTokenCaptor.getValue();

        assertNotEquals(result, savedRefreshToken.getToken());
        assertTrue(savedRefreshToken.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    public void testCreate_whenTryingToSaveExistingToken_thenThrowException() {
        when(refreshTokenRepository.findByUserIdAndDeviceId(anyLong(), anyString())).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(AlreadyExistsException.class, () -> {
            refreshTokenManager.create(USER_ID, DEVICE_ID);
        });

        verify(refreshTokenRepository).findByUserIdAndDeviceId(USER_ID, DEVICE_ID);
        verify(refreshTokenRepository).save(refreshToken);
        verifyNoInteractions(entityManager);
    }

    @Test
    public void testDeleteRefreshToken() {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(refreshTokenRepository.deleteRefreshTokenByTokenAndDeviceId(anyString(), anyString())).thenReturn(1);

        refreshTokenManager.deleteRefreshToken(REFRESH_TOKEN, DEVICE_ID);

        verify(refreshTokenRepository).deleteRefreshTokenByTokenAndDeviceId(stringArgumentCaptor.capture(), eq(DEVICE_ID));

        String usedToken = stringArgumentCaptor.getValue();

        assertNotEquals(REFRESH_TOKEN, usedToken);
    }

}
