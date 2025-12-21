package com.orderservice.controller;

import com.orderservice.dto.AdminOrderDto;
import com.orderservice.dto.AdminOrderPreviewDto;
import com.orderservice.dto.AdminOrderParams;
import com.orderservice.dto.UpdateContactInfoRequest;
import com.orderservice.enums.OrderAdminSortByEnum;
import com.orderservice.service.AdminOrderFacade;
import com.orderservice.service.OrderEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {
    private final AdminOrderFacade adminOrderService;
    private final OrderEventService orderEventService;


    @GetMapping("/{id}")
    public ResponseEntity<AdminOrderDto> getOrderById(
            @PathVariable("id") UUID id
    ) {
        AdminOrderDto order = adminOrderService.getOrder(id);

        return ResponseEntity.ok().body(order);
    }

    @GetMapping
    public ResponseEntity<Page<AdminOrderPreviewDto>> getAllOrders(
            AdminOrderParams body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "ID") OrderAdminSortByEnum sortBy
    ) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                sortBy.getField()
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdminOrderPreviewDto> orders = adminOrderService.getOrders(body, pageable);

        return ResponseEntity.ok().body(orders);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminOrderDto> updateContactInfo(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateContactInfoRequest body
    ) {
        AdminOrderDto order = adminOrderService.updateOrderContactInfo(id, body);

        return ResponseEntity.ok().body(order);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> completeOrder(
            @PathVariable long id
    ) {
        orderEventService.completeOrder(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/process")
    public ResponseEntity<Void> processOrder(
            @PathVariable long id
    ) {
        orderEventService.processOrder(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/ship")
    public ResponseEntity<Void> shipOrder(
            @PathVariable long id
    ) {
        orderEventService.shipOrder(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/pickup-ready")
    public ResponseEntity<Void> pickupReadyOrder(
            @PathVariable long id
    ) {
        orderEventService.pickupReadyOrder(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<Void> returnOrder(
            @PathVariable long id
    ) {
        orderEventService.returnOrder(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable long id
    ) {
        orderEventService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }
}
