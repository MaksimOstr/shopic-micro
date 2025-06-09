package com.productservice.repository;

import com.productservice.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByProduct_IdAndUserId(long productId, long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Like l WHERE l.product.id = :productId AND l.userId = :userId")
    int deleteByProduct_IdAndUserId(long productId, long userId);

    @Query("SELECT l.product.id FROM Like l WHERE l.userId = :userId")
    Set<Long> findLikedProductIds(long userId);

    int countByProduct_Id(long productId);
}
