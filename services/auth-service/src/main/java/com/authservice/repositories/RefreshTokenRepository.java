package com.authservice.repositories;

import com.authservice.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @EntityGraph(attributePaths = "user")
    Optional<RefreshToken> findByTokenAndDeviceId(String token, String deviceId);

    @EntityGraph(attributePaths = "user")
    Optional<RefreshToken> findByUserIdAndDeviceId(long userId, String deviceId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.token = :token AND r.deviceId = :deviceId")
    int deleteRefreshTokenByTokenAndDeviceId(String token, String deviceId);
}
