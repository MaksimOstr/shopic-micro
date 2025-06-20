package com.orderservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orderservice.config.security.model.CustomPrincipal;
import com.orderservice.dto.OrderDto;
import com.orderservice.dto.request.CreateOrderRequest;
import com.orderservice.entity.Order;
import com.orderservice.service.OrderCreationService;
import com.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @AuthenticationPrincipal CustomPrincipal principal,
            //Add additional info for order
            @RequestBody CreateOrderRequest body
    ) throws JsonProcessingException {
        orderCreationService.createOrder(principal.getId(), body);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderDto>> getUserOrders(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        List<OrderDto> orders = orderService.getOrdersByUserId(principal.getId());

        return ResponseEntity.ok().body(orders);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable String id
    ) {
        return ResponseEntity.ok().build();
    }
}
