package com.apigateway.config;

import com.apigateway.filters.JwtHandlerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class ApiGatewayConfig {
    @Bean
    public RouterFunction<ServerResponse> authRoute() {
        return route("auth-service-route")
                .path("/auth",  request -> {
                    request
                            .filter(lb("auth-service"))
                            .POST("/register", http());
                })
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("auth-service-route")
                .path("/users",  request -> {
                    request
                            .filter(jwtHandlerFilter)
                            .filter(lb("user-service"))
                            .POST("/request-email-verify", http());
                })
                .build();
    }
}
