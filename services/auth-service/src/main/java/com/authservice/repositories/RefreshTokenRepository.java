package com.authservice.repositories;

import com.authservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenAndDeviceId(String token, String deviceId);


    Optional<RefreshToken> findByUserIdAndDeviceId(long userId, String deviceId);
}
