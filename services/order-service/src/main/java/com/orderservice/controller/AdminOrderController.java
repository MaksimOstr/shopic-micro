package com.orderservice.controller;

import com.orderservice.dto.AdminOrderDto;
import com.orderservice.dto.AdminOrderPreviewDto;
import com.orderservice.dto.AdminOrderParams;
import com.orderservice.dto.UpdateContactInfoRequest;
import com.orderservice.dto.UpdateOrderStatusRequest;
import com.orderservice.enums.OrderAdminSortByEnum;
import com.orderservice.service.AdminOrderFacade;
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
@RequestMapping("/api/v1/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {
    private final AdminOrderFacade adminOrderService;


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

    @PatchMapping("/{id}/contact-info")
    public ResponseEntity<AdminOrderDto> updateContactInfo(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateContactInfoRequest body
    ) {
        AdminOrderDto order = adminOrderService.updateOrderContactInfo(id, body);

        return ResponseEntity.ok().body(order);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AdminOrderDto> updateOrderStatus(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateOrderStatusRequest body
    ) {
        AdminOrderDto order = adminOrderService.updateOrderStatus(id, body);

        return ResponseEntity.ok(order);
    }


}
