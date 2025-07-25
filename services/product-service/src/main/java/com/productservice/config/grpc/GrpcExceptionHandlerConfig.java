package com.productservice.config.grpc;

import com.productservice.exceptions.InsufficientStockException;
import com.productservice.exceptions.NotFoundException;
import io.grpc.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;


@Configuration(proxyBeanMethods = false)
public class GrpcExceptionHandlerConfig {

    @Bean
    GrpcExceptionHandler userServiceExceptionHandler() {
        return exception -> switch (exception) {
            case InsufficientStockException e -> Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asException();
            case NotFoundException e -> Status.NOT_FOUND.withDescription(e.getMessage()).asException();
            default -> null;
        };
    }
}
