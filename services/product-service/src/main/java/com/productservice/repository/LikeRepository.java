package com.productservice.repository;

import com.productservice.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {

    boolean existsByProduct_IdAndUserId(UUID productId, UUID userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Like l WHERE l.product.id = :productId AND l.userId = :userId")
    int deleteByProduct_IdAndUserId(UUID productId, UUID userId);

    @Query("SELECT l.product.id FROM Like l WHERE l.userId = :userId")
    Set<UUID> findLikedProductIds(UUID userId);
}
