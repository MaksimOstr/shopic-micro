package com.orderservice.service;

import com.orderservice.dto.AdminOrderDto;
import com.orderservice.dto.AdminOrderSummaryDto;
import com.orderservice.dto.request.AdminOrderParams;
import com.orderservice.entity.Order;
import com.orderservice.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.orderservice.utils.SpecificationUtils.*;


@Service
@RequiredArgsConstructor
public class AdminOrderService {
    private final OrderQueryService queryService;
    private final OrderMapper orderMapper;
    private final OrderEventService orderEventService;


    public AdminOrderDto getOrder(long orderId) {
        Order order = queryService.getOrderWithItems(orderId);

        return orderMapper.toAdminOrderDto(order);
    }

    @Transactional
    public Page<AdminOrderSummaryDto> getOrders(AdminOrderParams params, Pageable pageable) {
        Specification<Order> spec = iLikeNested("firstName", "customer" , params.firstName())
                .and(iLikeNested("lastName", "customer", params.lastName()))
                .and(equalsEnum("status", params.status()));
        Page<Order> orderPage = queryService.getOrdersBySpec(spec, pageable);
        List<Order> orderList = orderPage.getContent();
        List<AdminOrderSummaryDto> orderDtoList = orderMapper.toAdminOrderSummaryDto(orderList);

        return new PageImpl<>(orderDtoList, pageable, orderPage.getTotalElements());
    }

    public void completeOrder(long orderId) {
        orderEventService.completeOrder(orderId);
    }

    public void cancelOrder(long orderId) {
        orderEventService.cancelOrder(orderId);
    }

    public void processOrder(long orderId) {
        orderEventService.processOrder(orderId);
    }

    public void shipOrder(long orderId) {
        orderEventService.shipOrder(orderId);
    }

    public void pickupOrder(long orderId) {
        orderEventService.pickupReadyOrder(orderId);
    }
}
