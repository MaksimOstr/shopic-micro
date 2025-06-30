package com.orderservice.controller;

import com.orderservice.config.security.model.CustomPrincipal;
import com.orderservice.dto.OrderDto;
import com.orderservice.dto.request.CreateOrderRequest;
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
    public ResponseEntity<String> createOrder(
            @AuthenticationPrincipal CustomPrincipal principal,
            //Add additional info for order
            @RequestBody CreateOrderRequest body
    ) {
        String redirectUrl = orderCreationService.createOrder(principal.getId(), body);

        return ResponseEntity.ok(redirectUrl);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderDto> getOrder(
            @PathVariable Long id
    ) {
        OrderDto order = orderService.getOrderById(id);

        return ResponseEntity.ok().body(order);
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
