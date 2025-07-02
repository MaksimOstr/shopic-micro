package com.orderservice.service;

import com.orderservice.dto.OrderDto;
import com.orderservice.dto.OrderItemDto;
import com.orderservice.dto.OrderSummaryDto;
import com.orderservice.dto.request.OrderParams;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderItem;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.exception.NotFoundException;
import com.orderservice.mapper.OrderMapper;
import com.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.orderservice.utils.SpecificationUtils.*;
import static net.bytebuddy.matcher.ElementMatchers.hasChild;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderQueryService queryService;
    private final OrderMapper orderMapper;

    @Transactional
    public Page<OrderSummaryDto> getOrdersByUserId(long userId, Pageable pageable, OrderParams params) {
        Specification<Order> spec = equalsEnum("status", params.status())
                .and(hasId("userId", userId))
                .and(gte("totalPrice", params.fromPrice()))
                .and(equalsEnum("status", params.status()))
                .and(lte("totalPrice", params.toPrice()));
        Page<Order> orderPage = queryService.getOrdersBySpec(spec, pageable);
        List<Order> orderList = orderPage.getContent();
        List<OrderSummaryDto> orderSummaryList = orderMapper.toOrderSummaryDto(orderList);

        return new PageImpl<>(orderSummaryList, pageable, orderPage.getTotalElements());
    }

    public OrderDto getOrderById(long orderId) {
        Order order = queryService.getOrderWithItems(orderId);

        return orderMapper.toOrderDto(order);
    }

    public void changeOrderStatus(long orderId, OrderStatusEnum status) {
        int updated = orderRepository.changeOrderStatus(orderId, status);

        if(updated == 0) {
            log.error("Order status change failed");
            throw new NotFoundException("Order not found");
        }
    }
}
