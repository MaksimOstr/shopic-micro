package com.codeservice.repository;

import com.codeservice.entity.Code;
import com.codeservice.enums.CodeScopeEnum;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
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
    Optional<Code> findByCode(String code);
}
