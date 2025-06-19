package com.orderservice.service;

import com.orderservice.dto.OrderDto;
import com.orderservice.dto.OrderItemDto;
import com.orderservice.entity.Order;
import com.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<OrderDto> getOrdersByUserId(long userId) {
        List<Order> orders = orderRepository.findOrdersByUserId(userId);

        return orders.stream().map(order -> new OrderDto(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getOrderItems().stream().map(item -> new OrderItemDto(
                        item.getId(),
                        item.getQuantity(),
                        item.getProductName(),
                        item.getProductImageUrl(),
                        item.getProductId(),
                        item.getPriceAtPurchase()
                )).toList()
        )).toList();
    }
}
