package com.authservice.repositories;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Code c WHERE c.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredCodes();

    void deleteByUser_IdAndScope(UUID userId, CodeScopeEnum scope);

    Optional<Code> findByCodeAndScope(String code, CodeScopeEnum scope);
}
