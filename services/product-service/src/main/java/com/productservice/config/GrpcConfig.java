package com.productservice.config;

import com.productservice.exceptions.ApiException;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.http.HttpStatus;

@Slf4j
@Configuration
public class GrpcConfig {

    @Bean
    GrpcExceptionHandler grpcExceptionHandler() {
        return exception -> {
            log.error("GrpcExceptionHandlerConfig", exception);
            if(exception instanceof ApiException e) {
                HttpStatus status = e.getStatus();

                return switch(status) {
                    case NOT_FOUND -> Status.NOT_FOUND.withDescription(e.getMessage()).asException();
                    case CONFLICT -> Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asException();
                    default -> Status.INTERNAL.withDescription(e.getMessage()).asException();
                };
            }

            return Status.UNKNOWN.withDescription(exception.getMessage()).asException();
        };
    }
}
