package com.banservice.repository;

import com.banservice.entity.Ban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface BanRepository extends JpaRepository<Ban, Long>, JpaSpecificationExecutor<Ban> {

    boolean existsByUserIdAndActive(Long userId, boolean active);

    @Transactional
    @Modifying
    @Query("UPDATE Ban b SET b.isActive = false WHERE b.banTo < CURRENT_TIMESTAMP AND b.isActive = true")
    void deactivateExpiredBans();
}
