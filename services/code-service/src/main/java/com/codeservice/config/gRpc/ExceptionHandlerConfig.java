package com.codeservice.config.gRpc;

import com.codeservice.exception.CodeValidationException;
import com.codeservice.exception.NotFoundException;
import io.grpc.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;

@Configuration(proxyBeanMethods = false)
public class ExceptionHandlerConfig {

    @Bean
    GrpcExceptionHandler codeServiceExceptionHandler() {
        return exception -> switch (exception) {
            case DataIntegrityViolationException e -> Status.ALREADY_EXISTS.withDescription(e.getMessage()).asException();
            case CodeValidationException e -> Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asException();
            case IllegalArgumentException e -> Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asException();
            case NotFoundException e -> Status.NOT_FOUND.withDescription(e.getMessage()).asException();
            default -> null;
        };
    }
}
