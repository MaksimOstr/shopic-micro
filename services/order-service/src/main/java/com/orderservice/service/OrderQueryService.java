package com.orderservice.service;

import com.orderservice.entity.Order;
import com.orderservice.exception.NotFoundException;
import com.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderRepository orderRepository;

    public Order getOrderWithItems(long orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    public Order getOrderById(long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    public Page<Order> getOrdersBySpec(Specification<Order> spec, Pageable pageable) {
        return orderRepository.findAll(spec, pageable);
    }
}
