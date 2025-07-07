package com.paymentservice.controller;

import com.paymentservice.dto.PaymentDto;
import com.paymentservice.dto.PaymentSummaryDto;
import com.paymentservice.dto.RefundDto;
import com.paymentservice.dto.RefundSummaryDto;
import com.paymentservice.dto.request.PaymentParams;
import com.paymentservice.dto.request.RefundParams;
import com.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(
            @PathVariable int id
    ) {
        PaymentDto refund = paymentService.getPayment(id);

        return ResponseEntity.ok(refund);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentSummaryDto>> getPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestBody PaymentParams params
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, direction, "createdAt");
        Page<PaymentSummaryDto> refunds = paymentService.getPayments(params, pageable);

        return ResponseEntity.ok(refunds);
    }
}
