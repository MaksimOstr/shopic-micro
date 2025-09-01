package com.orderservice.controller;

import com.orderservice.config.security.model.CustomPrincipal;
import com.orderservice.dto.OrderDto;
import com.orderservice.dto.OrderSummaryDto;
import com.orderservice.dto.request.CreateOrderRequest;
import com.orderservice.dto.request.OrderParams;
import com.orderservice.service.OrderCreationService;
import com.orderservice.service.OrderEventService;
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

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/orders")
public class OrderController {
    private final OrderCreationService orderCreationService;
    private final OrderService orderService;
    private final OrderEventService orderEventService;


    @PostMapping
    public ResponseEntity<String> createOrder(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid CreateOrderRequest body
    ) {
        String redirectUrl = orderCreationService.createOrder(principal.getId(), body);

        return ResponseEntity.ok(redirectUrl);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(
            @PathVariable Long id
    ) {
        OrderDto order = orderService.getOrderById(id);

        return ResponseEntity.ok().body(order);
    }

    @GetMapping
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
            @PathVariable long id
    ) {
        orderEventService.cancelOrder(id);

        return ResponseEntity.ok().build();
    }
}
