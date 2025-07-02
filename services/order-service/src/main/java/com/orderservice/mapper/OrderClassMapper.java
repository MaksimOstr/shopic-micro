package com.orderservice.mapper;

import com.orderservice.dto.AdminOrderDto;
import com.orderservice.dto.OrderItemDto;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderClassMapper {
    public AdminOrderDto mapToDto(Order order) {
        List<OrderItemDto> items = order.getOrderItems().stream()
                .map(this::mapToItemDto)
                .toList();

        return new AdminOrderDto(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getUpdatedAt(),
                order.getCreatedAt(),
                items,
                order.getUserId()
        );
    }

    private OrderItemDto mapToItemDto(OrderItem item) {
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
