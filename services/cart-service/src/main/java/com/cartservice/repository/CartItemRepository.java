package com.cartservice.repository;

import com.cartservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    int countByCart_Id(UUID cartId);

    Optional<CartItem> findByCart_IdAndProductId(UUID cartId, Long productId);

    void deleteByCart_IdAndId(UUID cartId, UUID id);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id = :id")
    void deleteById(UUID id);
}
