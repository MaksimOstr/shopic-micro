package com.orderservice.service;

import com.orderservice.dto.OrderDto;
import com.orderservice.dto.OrderItemDto;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderItem;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.exception.NotFoundException;
import com.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<OrderDto> getOrdersByUserId(long userId) {
        List<Order> orders = orderRepository.findOrdersByUserId(userId);

        return orders.stream()
                .map(this::mapToOrderDto)
                .toList();
    }

    public OrderDto getOrderById(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        return mapToOrderDto(order);
    }

    public void changeOrderStatus(long orderId, OrderStatusEnum status) {
        int updated = orderRepository.changeOrderStatus(orderId, status);

        if(updated == 0) {
            log.error("Order status change failed");
            throw new NotFoundException("Order not found");
        }
    }


    private OrderDto mapToOrderDto(Order order) {
        List<OrderItemDto> items = order.getOrderItems().stream()
                .map(this::mapToOrderItemDto)
                .toList();

        return new OrderDto(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getUpdatedAt(),
                order.getCreatedAt(),
                items
        );
    }

    private OrderItemDto mapToOrderItemDto(OrderItem item) {
        return new OrderItemDto(
                item.getId(),
                item.getQuantity(),
                item.getProductName(),
                item.getProductImageUrl(),
                item.getProductId(),
                item.getPriceAtPurchase()
        );
    }
}
