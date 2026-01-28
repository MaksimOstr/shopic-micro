package com.paymentservice.service.grpc;


import com.paymentservice.dto.CreateCheckoutSessionDto;
import com.paymentservice.mapper.GrpcMapper;
import com.paymentservice.service.StripeCheckoutService;
import com.shopic.grpc.paymentservice.CreatePaymentRequest;
import com.shopic.grpc.paymentservice.CreatePaymentResponse;
import com.shopic.grpc.paymentservice.PaymentServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcPaymentService extends PaymentServiceGrpc.PaymentServiceImplBase {
    private final StripeCheckoutService stripeService;
    private final GrpcMapper grpcMapper;

    @Override
    public void createPayment(CreatePaymentRequest request, StreamObserver<CreatePaymentResponse> responseObserver) {
        log.info("Create payment request {}", request);

        CreateCheckoutSessionDto dto = new CreateCheckoutSessionDto(
                UUID.fromString(request.getOrderId()),
                UUID.fromString(request.getUserId()),
                grpcMapper.toCheckoutItemList(request.getOrderItemsList())
        );

        String redirectUrl = stripeService.createCheckoutSession(dto);

        CreatePaymentResponse response = CreatePaymentResponse.newBuilder()
                .setCheckoutUrl(redirectUrl)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
