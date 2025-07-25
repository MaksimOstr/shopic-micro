package com.reviewservice.config.security;

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
}
