package com.orderservice.config;

import com.orderservice.service.grpc.PaymentGrpcService;
import com.shopic.grpc.cartservice.CartServiceGrpc;
import com.shopic.grpc.paymentservice.PaymentServiceGrpc;
import com.shopic.grpc.productservice.ProductServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {

    @Bean
    ProductServiceGrpc.ProductServiceBlockingStub productServiceGrpc(GrpcChannelFactory channels) {
        return ProductServiceGrpc.newBlockingStub(channels.createChannel("product-service"));
    }

    @Bean
    CartServiceGrpc.CartServiceBlockingStub cartServiceGrpc(GrpcChannelFactory channels) {
        return CartServiceGrpc.newBlockingStub(channels.createChannel("cart-service"));
    }

    @Bean
    PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceGrpc(GrpcChannelFactory channels) {
        return PaymentServiceGrpc.newBlockingStub(channels.createChannel("payment-service"));
    }
}
