package com.orderservice.service;

import com.orderservice.entity.Order;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.exception.NotFoundException;
import com.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderRepository orderRepository;

    public Order getOrderWithItems(long orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    public List<Order> getOrdersByStatusAndCreatedAtBefore(OrderStatusEnum status, Instant createdAt) {
        return orderRepository.findByStatusAndCreatedAtBefore(status, createdAt);
    }

    public List<Order> getOrdersWithItems(long userId) {
        return orderRepository.findOrdersByUserId(userId);
    }
}
