package com.authservice.config;

import com.authservice.exceptions.NotFoundException;
import com.shopic.grpc.banservice.BanServiceGrpc;
import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.shopic.grpc.userservice.UserServiceGrpc;
import io.grpc.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;

@Configuration(proxyBeanMethods = false)
public class GrpcConfig {
    @Bean
    CodeServiceGrpc.CodeServiceBlockingStub codeServiceBlockingStub(GrpcChannelFactory channels) {
        return CodeServiceGrpc.newBlockingStub(channels.createChannel("code-service"));
    }

    @Bean
    BanServiceGrpc.BanServiceBlockingStub banServiceBlockingStub(GrpcChannelFactory channels) {
        return BanServiceGrpc.newBlockingStub(channels.createChannel("ban-service"));
    }

    @Bean
    GrpcExceptionHandler codeServiceExceptionHandler() {
        return exception -> switch (exception) {
            case NotFoundException e -> Status.NOT_FOUND.withDescription(e.getMessage()).asException();
            default -> null;
        };
    }
}
