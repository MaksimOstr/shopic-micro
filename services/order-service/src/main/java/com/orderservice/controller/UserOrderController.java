package com.orderservice.controller;

import com.orderservice.security.CustomPrincipal;
import com.orderservice.dto.UserOrderDto;
import com.orderservice.dto.UserOrderPreviewDto;
import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.dto.OrderParams;
import com.orderservice.service.UserOrderFacade;
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

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/v1/orders")
public class UserOrderController {
    private final UserOrderFacade userOrderFacade;


    @PostMapping
    public ResponseEntity<String> createOrder(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid CreateOrderRequest body
    ) {
        String redirectUrl = userOrderFacade.placeOrder(body, principal.getId());

        return ResponseEntity.ok(redirectUrl);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserOrderDto> getOrder(
            @PathVariable UUID id
    ) {
        UserOrderDto order = userOrderFacade.getUserOrderDtoById(id);

        return ResponseEntity.ok().body(order);
    }

    @GetMapping
    public ResponseEntity<Page<UserOrderPreviewDto>> getOrderPage(
            @AuthenticationPrincipal CustomPrincipal principal,
            OrderParams body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, direction, "createdAt");
        Page<UserOrderPreviewDto> orders = userOrderFacade.getOrdersByUserId(principal.getId(), pageable, body);

        return ResponseEntity.ok().body(orders);
    }
}
