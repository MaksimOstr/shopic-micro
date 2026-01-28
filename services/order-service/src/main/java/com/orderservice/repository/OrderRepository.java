package com.orderservice.repository;

import com.orderservice.entity.Order;
import com.orderservice.entity.OrderStatusEnum;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
    @EntityGraph(attributePaths = { "orderItems" })
    List<Order> findOrdersByUserId(UUID userId);

    @Transactional
    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :id")
    int changeOrderStatus(UUID id, OrderStatusEnum status);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems oi WHERE o.id = :id")
    Optional<Order> findByIdWithItems(UUID id);

    List<Order> findByStatusAndCreatedAtBefore(OrderStatusEnum status, Instant date);
}
