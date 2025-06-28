package com.paymentservice.service.grpc;


import com.paymentservice.dto.CreateCheckoutSessionDto;
import com.paymentservice.mapper.GrpcMapper;
import com.paymentservice.service.StripeService;
import com.shopic.grpc.paymentservice.CreatePaymentRequest;
import com.shopic.grpc.paymentservice.CreatePaymentResponse;
import com.shopic.grpc.paymentservice.PaymentServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcPaymentService extends PaymentServiceGrpc.PaymentServiceImplBase {
    private final StripeService stripeService;
    private final GrpcMapper grpcMapper;


    @Override
    public void createPaymentForOrder(CreatePaymentRequest request, StreamObserver<CreatePaymentResponse> responseObserver) {
        log.info("Create payment for order {}", request);

        CreateCheckoutSessionDto dto = new CreateCheckoutSessionDto(
                request.getOrderId(),
                request.getCustomerId(),
                request.getLineItemsList().stream().map(grpcMapper::toCheckoutItem).toList()
        );

        String redirectUrl = stripeService.createCheckoutSession(dto);

        CreatePaymentResponse response = CreatePaymentResponse.newBuilder()
                .setCheckoutUrl(redirectUrl)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
