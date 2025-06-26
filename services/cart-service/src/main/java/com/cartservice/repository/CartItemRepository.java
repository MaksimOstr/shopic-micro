package com.cartservice.repository;

import com.cartservice.entity.CartItem;
import com.cartservice.projection.CartItemForOrderProjection;
import com.cartservice.projection.CartItemProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT new com.cartservice.projection.CartItemProjection(" +
            "ci.id," +
            "ci.productId," +
            "ci.quantity," +
            "ci.priceAtAdd," +
            "ci.productName," +
            "ci.productImageUrl" +
            ")" +
            "FROM CartItem ci WHERE ci.cart.id = :cartId")
    List<CartItemProjection> findByCart_Id(Long cartId);

    int countByCart_Id(Long cartId);

    Optional<CartItem> findByCart_IdAndProductId(Long cartId, Long productId);

    @Query("SELECT ci.cart.id FROM CartItem ci WHERE ci.id = :cartItemId")
    Optional<Long> getCartIdByCartItemId(long cartItemId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id = :id")
    int deleteById(long id);

    @Query("SELECT new com.cartservice.projection.CartItemForOrderProjection(" +
            "ci.productId," +
            "ci.productName," +
            "ci.productImageUrl," +
            "ci.quantity" +
            ")" +
            "FROM CartItem ci WHERE ci.cart.id = :cartId")
    List<CartItemForOrderProjection> findCartItemForOrderByCartId(Long cartId);
}
