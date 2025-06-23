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
    public RouterFunction<ServerResponse> authRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("auth-service-route")
                .filter(lb("auth-service"))
                .path("/auth", request -> request
                        .GET("/login/oauth2/code/*", http())
                        .GET("/oauth2/authorization/*", http())
                        .POST("/register", http())
                        .POST("/refresh", http())
                        .POST("/sign-in", http()))
                .path("/auth", request -> request
                        .filter(jwtHandlerFilter)
                        .POST("/logout", http()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userRoute() {
        return route("user-service-route")
                .filter(lb("user-service"))
                .path("/verify", request -> request
                        .POST("/request", http())
                        .PATCH("", http()))
                .path("/forgot-password", request -> request
                        .POST("/request", http())
                        .PATCH("/reset", http()))
                .path("/password", request -> request
                        .PATCH("/change", http()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("product-service-route")
                .filter(lb("product-service"))
                .path("/likes", request -> request
                        .filter(jwtHandlerFilter)
                        .POST("", http()))
                .path("/products", request -> request
                        .filter(jwtHandlerFilter)
                        .GET("/active/{id}", http())
                        .GET("", http())
                        .GET("/liked", http())
                        .GET("/filter", http()))
                .path("/admin/products", request -> request
                        .filter(jwtHandlerFilter)
                        .POST("", http())
                        .GET("/{id}", http())
                        .GET("/sku", http())
                        .GET("/filter", http())
                        .PATCH("/{id}/image", http())
                        .PATCH("/{id}", http())
                        .DELETE("/{id}", http()))
                        .build();
    }

    @Bean
    public RouterFunction<ServerResponse> cartRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("cart-service-route")
                .filter(lb("cart-service"))
                .path("/carts", request -> request
                        .filter(jwtHandlerFilter)
                        .GET("/items", http())
                        .POST("/items", http())
                        .DELETE("/items", http())
                        .DELETE("", http())
                        .PATCH("/items/quantity", http()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("order-service-route")
                .filter(lb("order-service"))
                .path("/orders", request -> request
                        .filter(jwtHandlerFilter)
                        .POST("", http())
                        .GET("", http()))
                .build();
    }
}
