package com.authservice.repositories;

import com.authservice.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserIdAndDeviceId(long userId, String deviceId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.token = :token")
    int deleteRefreshTokenByTokenAndDeviceId(String token);
}
