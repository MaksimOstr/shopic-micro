package com.productservice.config.grpc;

import com.shopic.grpc.reviewservice.ReviewServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {

    @Bean
    ReviewServiceGrpc.ReviewServiceBlockingStub reviewServiceBlockingStub(GrpcChannelFactory channels) {
        return ReviewServiceGrpc.newBlockingStub(channels.createChannel("review-service"));
    }
}
