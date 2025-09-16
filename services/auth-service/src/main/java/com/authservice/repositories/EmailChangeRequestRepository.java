package com.authservice.repositories;

import com.authservice.entity.EmailChangeRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface EmailChangeRequestRepository extends JpaRepository<EmailChangeRequest, Long> {

    Optional<EmailChangeRequest> findByUser_Id(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM EmailChangeRequest e WHERE e.createdAt < :cutoff")
    int deleteAllByCreatedAtBefore(@Param("cutoff") Instant cutoff);
}
