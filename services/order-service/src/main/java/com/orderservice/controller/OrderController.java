package com.orderservice.controller;

import com.orderservice.config.security.model.CustomPrincipal;
import com.orderservice.dto.OrderDto;
import com.orderservice.dto.OrderSummaryDto;
import com.orderservice.dto.request.CreateOrderRequest;
import com.orderservice.dto.request.OrderParams;
import com.orderservice.service.OrderCreationService;
import com.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            @RequestBody @Valid CreateOrderRequest body
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
    public ResponseEntity<Page<OrderSummaryDto>> getUserOrders(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody OrderParams body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, direction, "createdAt");
        Page<OrderSummaryDto> orders = orderService.getOrdersByUserId(principal.getId(), pageable, body);

        return ResponseEntity.ok().body(orders);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable String id
    ) {

        return ResponseEntity.ok().build();
    }
}
