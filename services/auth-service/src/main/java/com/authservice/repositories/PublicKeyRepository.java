package com.authservice.repositories;

import com.authservice.entity.PublicKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface PublicKeyRepository extends JpaRepository<PublicKey, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM PublicKey k WHERE k.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredKeys();
}
