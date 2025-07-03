package com.orderservice.controller;

import com.orderservice.dto.AdminOrderDto;
import com.orderservice.dto.AdminOrderSummaryDto;
import com.orderservice.dto.request.AdminOrderParams;
import com.orderservice.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('USER')")
public class AdminOrderController {
    private final AdminOrderService adminOrderService;


    @GetMapping("/{id}")
    public ResponseEntity<AdminOrderDto> getOrderById(
            @PathVariable("id") int id
    ) {
        AdminOrderDto order = adminOrderService.getOrder(id);

        return ResponseEntity.ok().body(order);
    }

    @GetMapping
    public ResponseEntity<Page<AdminOrderSummaryDto>> getAllOrders(
            @RequestBody AdminOrderParams body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, direction, "createdAt");
        Page<AdminOrderSummaryDto> orders = adminOrderService.getOrders(body, pageable);

        return ResponseEntity.ok().body(orders);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> completeOrder(
            @PathVariable long id
    ) {
        adminOrderService.completeOrder(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable long id
    ) {
        adminOrderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/process")
    public ResponseEntity<Void> processOrder(
            @PathVariable long id
    ) {
        adminOrderService.processOrder(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/ship")
    public ResponseEntity<Void> shipOrder(
            @PathVariable long id
    ) {
        adminOrderService.shipOrder(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/pickup-ready")
    public ResponseEntity<Void> pickupReadyOrder(
            @PathVariable long id
    ) {
        adminOrderService.pickupOrder(id);
        return ResponseEntity.ok().build();
    }
}
