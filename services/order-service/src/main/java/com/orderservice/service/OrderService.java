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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderQueryService queryService;
    private final KafkaService kafkaService;

    public List<OrderDto> getOrdersByUserId(long userId) {
        List<Order> orders = queryService.getOrdersWithItems(userId);

        return orders.stream()
                .map(this::mapToOrderDto)
                .toList();
    }

    public OrderDto getOrderById(long orderId) {
        Order order = queryService.getOrderWithItems(orderId);

        return mapToOrderDto(order);
    }

    public void changeOrderStatus(long orderId, OrderStatusEnum status) {
        int updated = orderRepository.changeOrderStatus(orderId, status);

        if(updated == 0) {
            log.error("Order status change failed");
            throw new NotFoundException("Order not found");
        }
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 1000 * 60)
    @Transactional
    public void checkUnpaidOrders() {
        Instant thirtyMinutesAgo = Instant.now().minus(Duration.ofMinutes(30));
        queryService.getOrdersByStatusAndCreatedAtBefore(OrderStatusEnum.CREATED, thirtyMinutesAgo)
                .forEach(order -> {
                    order.setStatus(OrderStatusEnum.FAILED);
                    kafkaService.sendOrderCancelledEvent(order.getId());
                });
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
