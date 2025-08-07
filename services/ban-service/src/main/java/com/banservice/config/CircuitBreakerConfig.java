package com.banservice.config;

import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfig {
    @Bean
    public CircuitBreakerConfigCustomizer ignoreCertainGrpcStatuses() {
        return CircuitBreakerConfigCustomizer
                .of("auth-service", builder -> builder
                        .recordException(ex -> {
                            if (ex instanceof StatusRuntimeException statusEx) {
                                Status.Code code = statusEx.getStatus().getCode();
                                return switch (code) {
                                    case NOT_FOUND -> false;
                                    default -> true;
                                };
                            }
                            return true;
                        })
                );
    }
}
