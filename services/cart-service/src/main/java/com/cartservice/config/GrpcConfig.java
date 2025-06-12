package com.cartservice.config;

import com.shopic.grpc.productservice.ProductServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration(proxyBeanMethods = false)
public class GrpcConfig {
    @Bean
    ProductServiceGrpc.ProductServiceBlockingStub productServiceGrpc(GrpcChannelFactory channels) {
        return ProductServiceGrpc.newBlockingStub(channels.createChannel("product-service"));
    }
}
