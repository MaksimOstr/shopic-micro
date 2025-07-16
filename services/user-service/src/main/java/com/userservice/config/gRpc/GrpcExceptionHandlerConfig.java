package com.userservice.config.gRpc;

import com.userservice.exceptions.AlreadyExistsException;
import com.userservice.exceptions.NotFoundException;
import io.grpc.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;

@Configuration(proxyBeanMethods = false)
public class GrpcExceptionHandlerConfig {

    @Bean
    GrpcExceptionHandler userServiceExceptionHandler() {
        return exception -> switch (exception) {
            case DataIntegrityViolationException e -> Status.ALREADY_EXISTS.withDescription(e.getMessage()).asException();
            case AlreadyExistsException e -> Status.ALREADY_EXISTS.withDescription(e.getMessage()).asException();
            case NotFoundException e -> Status.NOT_FOUND.withDescription(e.getMessage()).asException();
            default -> null;
        };
    }
}
