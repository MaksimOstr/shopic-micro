package com.cartservice.config;

import com.cartservice.exception.ApiException;
import com.shopic.grpc.productservice.ProductServiceGrpc;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.http.HttpStatus;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class GrpcConfig {
    @Bean
    ProductServiceGrpc.ProductServiceBlockingStub productServiceGrpc(GrpcChannelFactory channels) {
        return ProductServiceGrpc.newBlockingStub(channels.createChannel("product-service"));
    }

    @Bean
    GrpcExceptionHandler grpcExceptionHandler() {
        return exception -> {
            log.error("GrpcExceptionHandlerConfig", exception);
            if(exception instanceof IllegalArgumentException) {
                return Status.INVALID_ARGUMENT.withDescription(exception.getMessage()).asException();
            }

            if(exception instanceof ApiException e) {
                HttpStatus status = e.getStatus();

                return switch(status) {
                    case NOT_FOUND -> Status.NOT_FOUND.withDescription(e.getMessage()).asException();
                    default -> Status.INTERNAL.withDescription(e.getMessage()).asException();
                };
            }

            return Status.UNKNOWN.withDescription(exception.getMessage()).asException();
        };
    }
}
