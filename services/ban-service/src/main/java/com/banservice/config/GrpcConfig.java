package com.banservice.config;

import com.shopic.grpc.authservice.AuthServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {
    @Bean
    AuthServiceGrpc.AuthServiceBlockingStub codeServiceBlockingStub(GrpcChannelFactory channels) {
        return AuthServiceGrpc.newBlockingStub(channels.createChannel("auth-service"));
    }
}
