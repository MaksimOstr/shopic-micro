package com.cartservice.repository;

import com.cartservice.entity.Cart;
import com.cartservice.projection.CartItemForOrderProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c.id FROM Cart c WHERE c.userId = :userId")
    Optional<Long> findCartIdByUserId(long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.id = :id")
    void deleteById(long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.userId = :userId")
    int deleteCartByUserId(long userId);

    @Query("SELECT DISTINCT c FROM Cart c JOIN FETCH c.cartItems WHERE c.userId = :userId")
    Optional<Cart> getCartWithItems(long userId);
}
