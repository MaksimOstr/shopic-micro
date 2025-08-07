package com.orderservice.config;

import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfig {
    @Bean
    public CircuitBreakerConfigCustomizer cartServiceCustomizer() {
        return CircuitBreakerConfigCustomizer
                .of("cart-service", builder -> builder
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

    @Bean
    public CircuitBreakerConfigCustomizer paymentServiceCustomizer() {
        return CircuitBreakerConfigCustomizer
                .of("payment-service", builder -> builder
                        .recordException(ex -> {
                            if (ex instanceof StatusRuntimeException statusEx) {
                                Status.Code code = statusEx.getStatus().getCode();
                                return switch (code) {
                                    case INTERNAL, FAILED_PRECONDITION -> false;
                                    default -> true;
                                };
                            }
                            return true;
                        })
                );
    }

    @Bean
    public CircuitBreakerConfigCustomizer productServiceCustomizer() {
        return CircuitBreakerConfigCustomizer
                .of("product-service", builder -> builder
                        .recordException(ex -> {
                            if (ex instanceof StatusRuntimeException statusEx) {
                                Status.Code code = statusEx.getStatus().getCode();
                                return switch (code) {
                                    case NOT_FOUND, FAILED_PRECONDITION -> false;
                                    default -> true;
                                };
                            }
                            return true;
                        })
                );
    }
}
