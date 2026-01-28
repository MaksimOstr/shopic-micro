package com.paymentservice.controller;

import com.paymentservice.dto.ErrorResponseDto;
import com.paymentservice.dto.PaymentDto;
import com.paymentservice.dto.PaymentSummaryDto;
import com.paymentservice.dto.PaymentParams;
import com.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Find payments by id",
            description = "Returns found product dto"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment successfully found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(
            @PathVariable UUID id
    ) {
        PaymentDto refund = paymentService.getPaymentDtoById(id);

        return ResponseEntity.ok(refund);
    }

    @Operation(
            summary = "Search payments by params",
            description = "Returns a page of payments"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Found payments"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
    })
    @GetMapping
    public ResponseEntity<Page<PaymentSummaryDto>> getPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @ModelAttribute PaymentParams params
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, direction, "createdAt");
        Page<PaymentSummaryDto> refunds = paymentService.getPayments(params, pageable);

        return ResponseEntity.ok(refunds);
    }
}
