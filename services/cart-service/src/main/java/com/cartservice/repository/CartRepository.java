package com.cartservice.repository;

import com.cartservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c.id FROM Cart c WHERE c.userId = :userId")
    Optional<Long> findCartIdByUserId(long userId);
}
