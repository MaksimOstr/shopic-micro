package com.orderservice.repository;

import com.orderservice.entity.Order;
import com.orderservice.entity.OrderStatusEnum;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @EntityGraph(attributePaths = { "orderItems" })
    List<Order> findOrdersByUserId(long userId);

    @Transactional
    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :id")
    int changeOrderStatus(long id, OrderStatusEnum status);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems oi WHERE o.id = :id")
    Optional<Order> findByIdWithItems(long id);

    @Modifying
    @Query("UPDATE Order o SET o.refunded = :value WHERE o.id = :id")
    int updateIsRefunded(long id, boolean value);

    List<Order> findByStatusAndCreatedAtBefore(OrderStatusEnum status, Instant date);
}
