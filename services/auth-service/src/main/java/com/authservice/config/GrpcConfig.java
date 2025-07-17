package com.authservice.config;

import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.shopic.grpc.userservice.UserServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration(proxyBeanMethods = false)
public class GrpcConfig {
    @Bean
    CodeServiceGrpc.CodeServiceBlockingStub codeServiceBlockingStub(GrpcChannelFactory channels) {
        return CodeServiceGrpc.newBlockingStub(channels.createChannel("code-service"));
    }
}
