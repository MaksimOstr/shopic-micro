package com.orderservice.service;

import com.orderservice.dto.AdminOrderDto;
import com.orderservice.dto.OrderItemDto;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderService {
    private final OrderQueryService queryService;


    public AdminOrderDto getOrder(long orderId) {
        Order order = queryService.getOrderWithItems(orderId);

        return mapToDto(order);
    }


    private AdminOrderDto mapToDto(Order order) {
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
