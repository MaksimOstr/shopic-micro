package com.paymentservice.controller;

import com.paymentservice.dto.RefundDto;
import com.paymentservice.dto.RefundSummaryDto;
import com.paymentservice.dto.request.FullRefundRequest;
import com.paymentservice.dto.request.PartialRefundRequest;
import com.paymentservice.dto.request.RefundParams;
import com.paymentservice.service.RefundService;
import com.paymentservice.service.StripeRefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/refunds")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RefundController {
    private final RefundService refundService;
    private final StripeRefundService stripeRefundService;


    @GetMapping("/{id}")
    public ResponseEntity<RefundDto> getRefundById(
            @PathVariable int id
    ) {
        RefundDto refund = refundService.getRefund(id);

        return ResponseEntity.ok(refund);
    }

    @GetMapping
    public ResponseEntity<Page<RefundSummaryDto>> getRefunds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestBody RefundParams params
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        Page<RefundSummaryDto> refunds = refundService.getRefunds(params, pageable);

        return ResponseEntity.ok(refunds);
    }

    @PostMapping("/full-refund")
    public ResponseEntity<Void> fullRefund(
            @RequestBody @Valid FullRefundRequest body
    ) {
        stripeRefundService.processFullRefund(body);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/partial-refund")
    public ResponseEntity<Void> partialRefund(
            @RequestBody @Valid PartialRefundRequest body
    ) {
        stripeRefundService.processPartialRefund(body);

        return ResponseEntity.ok().build();
    }
}
