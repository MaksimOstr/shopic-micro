package com.cartservice.repository;

import com.cartservice.entity.Cart;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems WHERE c.userId = :userId")
    Optional<Cart> findCartWithItemsByUserId(UUID userId);

    Optional<Cart> findCartByUserId(UUID userId);

    @Query("SELECT c.id FROM Cart c WHERE c.userId = :userId")
    Optional<UUID> findCartIdByUserId(UUID userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.id = :id")
    void deleteById(UUID id);

    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.userId = :userId")
    void deleteCartByUserId(UUID userId);

    <T> ScopedValue<T> findCartWByUserId(UUID userId, Sort sort, Limit limit);
}
