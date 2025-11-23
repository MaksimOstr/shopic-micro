package com.orderservice.controller;

import com.orderservice.config.security.model.CustomPrincipal;
import com.orderservice.dto.UserOrderDto;
import com.orderservice.dto.UserOrderPreviewDto;
import com.orderservice.dto.request.CreateOrderRequest;
import com.orderservice.dto.request.OrderParams;
import com.orderservice.service.UserOrderService;
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
public class UserOrderController {
    private final UserOrderService userOrderService;


    @PostMapping
    public ResponseEntity<String> createOrder(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid CreateOrderRequest body
    ) {
        String redirectUrl = userOrderService.createOrder(principal.getId(), body);

        return ResponseEntity.ok(redirectUrl);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserOrderDto> getOrder(
            @PathVariable Long id
    ) {
        UserOrderDto order = userOrderService.getUserOrderDtoById(id);

        return ResponseEntity.ok().body(order);
    }

    @GetMapping("/me")
    public ResponseEntity<Page<UserOrderPreviewDto>> getOrderPage(
            @AuthenticationPrincipal CustomPrincipal principal,
            OrderParams body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, direction, "createdAt");
        Page<UserOrderPreviewDto> orders = userOrderService.getOrdersByUserId(principal.getId(), pageable, body);

        return ResponseEntity.ok().body(orders);
    }
}
