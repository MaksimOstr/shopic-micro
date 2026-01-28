package com.apigateway.config;

import com.apigateway.config.properties.ServicesProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;


@Configuration
@RequiredArgsConstructor
public class ApiGatewayConfig {
    private final ServicesProperties servicesProperties;

    private static final String[] authServiceUrlPatterns = {
            "/auth/oauth2/authorization/**",
            "/auth/login/oauth2/code/**",
            "/api/v1/auth/**",
            "/api/v1/forgot-password/**",
            "/api/v1/users/**",
            "/api/v1/verification/**"
    };

    private static final String[] productServiceUrlPatterns = {
            "/api/v1/products/**",
            "/api/v1/admin/products/**",
            "/api/v1/admin/brands/**",
            "/api/v1/brands/**",
            "/api/v1/admin/categories/**",
            "/api/v1/categories/**",
            "/api/v1/reservations/**"
    };

    private static final String[] orderServiceUrlPatterns = {
            "/api/v1/admin/orders/**",
            "/api/v1/orders/**"
    };

    @Bean
    public RouterFunction<ServerResponse> authRoute() {
        return route("auth-service-route")
                .route(path(authServiceUrlPatterns), http(servicesProperties.getAuthUrl()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productRoute() {
        return route("product-service-route")
                .route(path(productServiceUrlPatterns), http(servicesProperties.getProductUrl()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderRoute() {
        return route("order-service-route")
                .route(path(orderServiceUrlPatterns), http(servicesProperties.getOrderUrl()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentRoute() {
        return route("payment-service-route")
                .route(path("/api/v1/payments/**"), http(servicesProperties.getPaymentUrl()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> cartRoute() {
        return route("cart-service-route")
                .route(path("/api/v1/carts/**"), http(servicesProperties.getCartUrl()))
                .build();
    }
}
