package com.orderservice.service;

import com.orderservice.dto.AdminOrderDto;
import com.orderservice.dto.AdminOrderSummaryDto;
import com.orderservice.dto.request.AdminOrderParams;
import com.orderservice.dto.request.UpdateContactInfoRequest;
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
import java.util.Optional;

import static com.orderservice.utils.SpecificationUtils.*;


@Service
@RequiredArgsConstructor
public class AdminOrderService {
    private final OrderQueryService queryService;
    private final OrderMapper orderMapper;


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

    @Transactional
    public AdminOrderDto updateOrderContactInfo(long orderId, UpdateContactInfoRequest dto) {
        Order order = queryService.getOrderById(orderId);

        Optional.ofNullable(dto.city()).ifPresent(city -> order.getAddress().setCity(city));
        Optional.ofNullable(dto.country()).ifPresent(country -> order.getAddress().setCountry(country));
        Optional.ofNullable(dto.street()).ifPresent(street -> order.getAddress().setStreet(street));
        Optional.ofNullable(dto.postalCode()).ifPresent(postalCode -> order.getAddress().setPostalCode(postalCode));
        Optional.ofNullable(dto.houseNumber()).ifPresent(houseNumber -> order.getAddress().setHouseNumber(houseNumber));
        Optional.ofNullable(dto.firstName()).ifPresent(firstName -> order.getCustomer().setFirstName(firstName));
        Optional.ofNullable(dto.lastName()).ifPresent(lastName -> order.getCustomer().setLastName(lastName));
        Optional.ofNullable(dto.phoneNumber()).ifPresent(phoneNumber -> order.getCustomer().setPhoneNumber(phoneNumber));

        return orderMapper.toAdminOrderDto(order);
    }
}
