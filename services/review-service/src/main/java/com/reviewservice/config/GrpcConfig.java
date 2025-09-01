package com.reviewservice.config;

import com.shopic.grpc.productservice.ProductServiceGrpc;
import com.shopic.grpc.profileservice.ProfileServiceGrpc;
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
    ProfileServiceGrpc.ProfileServiceBlockingStub profileServiceGrpc(GrpcChannelFactory channels) {
        return ProfileServiceGrpc.newBlockingStub(channels.createChannel("profile-service"));
    }
}
