package com.orderservice.service;

import com.orderservice.dto.UpdateContactInfoRequest;
import com.orderservice.entity.Order;
import com.orderservice.exception.NotFoundException;
import com.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;



@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private static final String ORDER_NOT_FOUND = "Order not found";

    @Transactional
    public Order updateOrderContactInfo(long orderId, UpdateContactInfoRequest dto) {
        Order order = getOrderById(orderId);

        Optional.ofNullable(dto.city()).ifPresent(city -> order.getAddress().setCity(city));
        Optional.ofNullable(dto.country()).ifPresent(country -> order.getAddress().setCountry(country));
        Optional.ofNullable(dto.street()).ifPresent(street -> order.getAddress().setStreet(street));
        Optional.ofNullable(dto.postalCode()).ifPresent(postalCode -> order.getAddress().setPostalCode(postalCode));
        Optional.ofNullable(dto.houseNumber()).ifPresent(houseNumber -> order.getAddress().setHouseNumber(houseNumber));
        Optional.ofNullable(dto.firstName()).ifPresent(firstName -> order.getCustomer().setFirstName(firstName));
        Optional.ofNullable(dto.lastName()).ifPresent(lastName -> order.getCustomer().setLastName(lastName));
        Optional.ofNullable(dto.phoneNumber()).ifPresent(phoneNumber -> order.getCustomer().setPhoneNumber(phoneNumber));

        return order;
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public void changeRefundStatus(long orderId, boolean isRefunded) {
        int updated = orderRepository.updateIsRefunded(orderId, isRefunded);

        if(updated == 0) {
            throw new NotFoundException(ORDER_NOT_FOUND);
        }
    }

    public Order getOrderWithItems(long orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));
    }

    public Order getOrderById(long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));
    }

    public Page<Order> getOrdersBySpec(Specification<Order> spec, Pageable pageable) {
        return orderRepository.findAll(spec, pageable);
    }
}
