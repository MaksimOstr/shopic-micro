package com.apigateway.config;

import com.apigateway.filters.JwtHandlerFilter;
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

    @Bean
    public RouterFunction<ServerResponse> authRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("auth-service-route")
                .route(path("/api/v1/auth/**"), http(servicesProperties.getAuthUrl()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("product-service-route")
                .route(path("/products/**", "/brands/**", "/categories/**"), http(servicesProperties.getProductUrl()))
                .filter(circuitBreaker(""))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("order-service-route")
                .route(path("/orders/**"), http(servicesProperties.getOrderUrl()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("payment-service-route")
                .route(path("/payments/**"), http(servicesProperties.getPaymentUrl()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> cartRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("cart-service-route")
                .route(path("/carts/**"), http(servicesProperties.getCartUrl()))
                .build();
    }
}
