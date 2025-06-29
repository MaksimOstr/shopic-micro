package com.orderservice.controller;

import com.orderservice.dto.AdminOrderDto;
import com.orderservice.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('USER')")
public class AdminOrderController {
    private final AdminOrderService adminOrderService;

    @GetMapping
    public ResponseEntity<?> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size

    ) {

    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminOrderDto> getOrderById(
            @PathVariable("id") int id
    ) {

    }
}
