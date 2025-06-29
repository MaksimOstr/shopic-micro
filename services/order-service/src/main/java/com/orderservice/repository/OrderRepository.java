package com.orderservice.repository;

import com.orderservice.entity.Order;
import com.orderservice.entity.OrderStatusEnum;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @EntityGraph(attributePaths = { "orderItems" })
    List<Order> findOrdersByUserId(long userId);

    @Transactional
    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :id")
    int changeOrderStatus(long id, OrderStatusEnum status);
}
