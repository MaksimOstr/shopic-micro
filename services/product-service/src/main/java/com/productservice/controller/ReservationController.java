package com.productservice.controller;

import com.productservice.dto.ReservationDto;
import com.productservice.dto.ReservationPreviewDto;
import com.productservice.enums.ReservationStatusEnum;
import com.productservice.enums.ReservationAdminSortByEnum;
import com.productservice.services.ReservationService;
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
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservation(
            @PathVariable UUID id
    ) {
        ReservationDto reservation = reservationService.getReservationAdminDto(id);

        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<ReservationDto> getReservationByOrderId(
            @PathVariable UUID orderId
    ) {
        ReservationDto reservation = reservationService.getReservationAdminDtoByOrderId(orderId);

        return ResponseEntity.ok(reservation);
    }

    @GetMapping
    public ResponseEntity<Page<ReservationPreviewDto>> getReservationPage(
            @RequestParam(required = false) ReservationStatusEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "CREATED_AT") ReservationAdminSortByEnum sortBy
    ) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection),
                sortBy.getField()
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReservationPreviewDto> reservationPage = reservationService.getReservationList(pageable, status);

        return ResponseEntity.ok(reservationPage);
    }
}
