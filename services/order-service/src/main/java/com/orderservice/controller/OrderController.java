package com.orderservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orderservice.config.security.model.CustomPrincipal;
import com.orderservice.dto.OrderDto;
import com.orderservice.entity.Order;
import com.orderservice.service.OrderCreationService;
import com.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderCreationService orderCreationService;
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Order> createOrder(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        orderCreationService.createOrder(principal.getId());

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderDto>> getOrders(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        List<OrderDto> orders = orderService.getOrdersByUserId(principal.getId());

        return ResponseEntity.ok().body(orders);
    }
}
