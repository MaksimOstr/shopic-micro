package com.paymentservice.service;

import com.paymentservice.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefundService {
    private final RefundRepository refundRepository;
}
