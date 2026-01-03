package com.orderservice.service;

import com.orderservice.dto.AdminOrderDto;
import com.orderservice.dto.AdminOrderPreviewDto;
import com.orderservice.dto.AdminOrderParams;
import com.orderservice.dto.UpdateContactInfoRequest;
import com.orderservice.dto.UpdateOrderStatusRequest;
import com.orderservice.entity.*;
import com.orderservice.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.orderservice.utils.SpecificationUtils.*;


@Service
@RequiredArgsConstructor
public class AdminOrderFacade {
    private final OrderService orderService;
    private final OrderMapper orderMapper;


    public AdminOrderDto getOrder(UUID orderId) {
        Order order = orderService.getOrderWithItems(orderId);

        return orderMapper.toAdminOrderDto(order);
    }

    @Transactional
    public Page<AdminOrderPreviewDto> getOrders(AdminOrderParams params, Pageable pageable) {
        Specification<Order> spec = iLikeNested("firstName", "customer", params.firstName())
                .and(hasId("userId", params.userId()))
                .and(iLikeNested("lastName", "customer", params.lastName()))
                .and(equalsEnum("status", params.status()));
        Page<Order> orderPage = orderService.getOrdersBySpec(spec, pageable);
        List<Order> orderList = orderPage.getContent();
        List<AdminOrderPreviewDto> orderDtoList = orderMapper.toAdminOrderSummaryDto(orderList);

        return new PageImpl<>(orderDtoList, pageable, orderPage.getTotalElements());
    }

    @Transactional
    public AdminOrderDto updateOrderContactInfo(UUID orderId, UpdateContactInfoRequest dto) {
        Order updatedOrder = orderService.updateOrderContactInfo(orderId, dto);

        return orderMapper.toAdminOrderDto(updatedOrder);
    }

    public AdminOrderDto updateOrderStatus(UUID orderId, UpdateOrderStatusRequest dto) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, dto.targetStatus());

        return orderMapper.toAdminOrderDto(updatedOrder);
    }
}
