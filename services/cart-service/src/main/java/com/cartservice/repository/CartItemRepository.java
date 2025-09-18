package com.cartservice.repository;

import com.cartservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    int countByCart_Id(Long cartId);

    @Query("SELECT ci.cart.id FROM CartItem ci WHERE ci.id = :cartItemId")
    Optional<Long> getCartIdByCartItemId(long cartItemId);

    Optional<CartItem> findByCart_IdAndProductId(Long cartId, Long productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id = :id")
    void deleteById(long id);
}
