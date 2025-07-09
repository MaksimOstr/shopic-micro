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
    public RouterFunction<ServerResponse> userRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("user-service-route")
                .filter(lb("user-service"))
                .path("/verify", request -> request
                        .POST("/request", http())
                        .PATCH("", http()))
                .path("/forgot-password", request -> request
                        .POST("/request", http())
                        .PATCH("/reset", http()))
                .path("/password", request -> request
                        .filter(jwtHandlerFilter)
                        .PATCH("/change", http()))
                .path("/email", request -> request
                        .filter(jwtHandlerFilter)
                        .POST("/change-request", http())
                        .PATCH("/change", http()))
                .path("/admin/users", request -> request
                        .filter(jwtHandlerFilter)
                        .GET("/{id}", http())
                        .GET("", http()))
                .path("/profiles", request -> request
                        .filter(jwtHandlerFilter)
                        .GET("", http())
                        .PATCH("", http()))
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
                        .DELETE("/items/{id}", http())
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
                .path("/admin/orders", request -> request
                        .filter(jwtHandlerFilter)
                        .GET("{id}", http())
                        .GET("", http())
                        .PATCH("/{id}/update-contact-info", http())
                        .PATCH("/{id}/complete", http())
                        .PATCH("/{id}/cancel", http())
                        .PATCH("/{id}/process", http())
                        .PATCH("/{id}/ship", http())
                        .PATCH("/{id}/pickup-ready", http()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentServiceRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("payment-service-route")
                .filter(lb("payment-service"))
                .path("/stripe", request -> request
                        .POST("/webhook", http()))
                .path("/payments", request -> request
                        .filter(jwtHandlerFilter)
                        .GET("", http())
                        .GET("/{id}", http()))
                .path("/refunds", request -> request
                        .filter(jwtHandlerFilter)
                        .POST("/full-refund", http())
                        .POST("/partial-refund", http()))
                        .GET("", http())
                        .GET("/{id}", http())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> reviewServiceRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("review-service-route")
                .filter(lb("review-service"))
                .path("/reviews", request -> request
                        .filter(jwtHandlerFilter)
                        .GET("", http())
                        .POST("", http())
                        .POST("/{id}/comment", http())
                        .GET("/{id}/comments", http()))
                .build();
    }
}
