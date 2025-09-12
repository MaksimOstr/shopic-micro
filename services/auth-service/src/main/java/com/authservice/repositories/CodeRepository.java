package com.authservice.repositories;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Code c WHERE c.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredCodes();

    @Transactional
    @Modifying
    @Query("DELETE FROM Code c WHERE c.code = :code")
    void deleteCodeByCode(String code);

    Optional<Code> findByScopeAndUserId(CodeScopeEnum scope, long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Code c WHERE c.code = :code AND c.used = false")
    Optional<Code> findUnusedByCode(String code);
}
