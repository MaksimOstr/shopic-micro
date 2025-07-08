package com.paymentservice.config.grpc;

import com.paymentservice.exception.InternalException;
import io.grpc.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;

@Configuration(proxyBeanMethods = false)
public class GrpcExceptionHandlerConfig {
    @Bean
    GrpcExceptionHandler exceptionHandler() {
        return exception -> switch (exception) {
            case DataIntegrityViolationException e -> Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asException();
            case InternalException e -> Status.INTERNAL.withDescription(e.getMessage()).asException();
            default -> null;
        };
    }
}
