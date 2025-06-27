package com.paymentservice.service.grpc;


import com.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcPaymentService {
    private final PaymentService paymentService;
}
