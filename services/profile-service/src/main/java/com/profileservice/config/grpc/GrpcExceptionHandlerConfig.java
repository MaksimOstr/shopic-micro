package com.profileservice.config.grpc;

import com.profileservice.exception.NotFoundException;
import io.grpc.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;

@Configuration(proxyBeanMethods = false)
public class GrpcExceptionHandlerConfig {

    @Bean
    GrpcExceptionHandler userServiceExceptionHandler() {
        return exception -> switch (exception) {
            case NotFoundException e -> Status.NOT_FOUND.withDescription(e.getMessage()).asException();
            default -> null;
        };
    }
}
