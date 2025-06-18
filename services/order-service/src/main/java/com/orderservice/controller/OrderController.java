package com.orderservice.controller;

import com.orderservice.config.security.model.CustomPrincipal;
import com.orderservice.entity.Order;
import com.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        orderService.createOrder(principal.getId());

        return ResponseEntity.ok().build();
    }
}
