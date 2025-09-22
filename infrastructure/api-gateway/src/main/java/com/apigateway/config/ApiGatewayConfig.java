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
                .path("/admin", request -> request
                        .filter(jwtHandlerFilter)
                        .path("/users", req -> req
                                .GET("/{id}", http())
                                .GET("", http())))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> productRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("product-service-route")
                .filter(lb("product-service"))
                .path("/likes", request -> request
                        .filter(jwtHandlerFilter)
                        .POST("", http())
                        .GET("/count", http()))
                .path("/products", request -> request
                        .filter(jwtHandlerFilter)
                        .GET("/{id}", http())
                        .GET("", http())
                        .GET("/liked", http()))
                .path("/brands", request -> request
                        .GET("/search", http()))
                .path("/categories", request -> request
                        .GET("/search", http()))
                .path("/admin", request -> request
                        .filter(jwtHandlerFilter)
                        .path("/products", req -> req
                                .POST("", http())
                                .GET("/{id}", http())
                                .GET("/sku/{sku}", http())
                                .GET("", http())
                                .PATCH("/{id}/image", http())
                                .PATCH("/{id}", http()))
                        .path("/brands", req -> req
                                .PATCH("/{id}", http())
                                .GET("/search", http())
                                .POST("", http())
                                .PATCH("/{id}/deactivate", http())
                                .PATCH("/{id}/activate", http()))
                        .path("/categories", req -> req
                                .GET("/search", http())
                                .POST("", http())
                                .PATCH("/{id}", http())
                                .PATCH("/{id}/deactivate", http())
                                .PATCH("/{id}/activate", http())))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> cartRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("cart-service-route")
                .filter(lb("cart-service"))
                .path("/carts", request -> request
                        .filter(jwtHandlerFilter)
                        .GET("/me", http())
                        .POST("/me/items", http())
                        .DELETE("/me/items/{id}", http())
                        .DELETE("/me", http())
                        .PATCH("/me/items/{id}/quantity", http()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("order-service-route")
                .filter(lb("order-service"))
                .path("/orders", request -> request
                        .filter(jwtHandlerFilter)
                        .POST("", http())
                        .GET("", http())
                        .GET("/{id}", http()))
                .path("/admin/orders", request -> request
                        .filter(jwtHandlerFilter)
                        .GET("/{id}", http())
                        .GET("", http())
                        .PATCH("/{id}", http())
                        .PATCH("/{id}/complete", http())
                        .PATCH("/{id}/cancel", http())
                        .PATCH("/{id}/process", http())
                        .PATCH("/{id}/ship", http())
                        .PATCH("/{id}/return", http())
                        .PATCH("/{id}/pickup-ready", http()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentServiceRoute(JwtHandlerFilter jwtHandlerFilter) {
        return route("payment-service-route")
                .filter(lb("payment-service"))
                .path("/stripe", builder -> builder
                        .POST("/webhook", http()))
                .path("/payments", builder -> builder
                        .filter(jwtHandlerFilter)
                        .GET("", http())
                        .GET("/{id}", http()))
                .path("/refunds", builder -> builder
                        .filter(jwtHandlerFilter)
                        .POST("/full-refund", http())
                        .POST("/partial-refund", http())
                        .GET("", http())
                        .GET("/{id}", http()))
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
                        .PATCH("/{id}", http())
                        .DELETE("/{id}", http()))
                .path("/review-comments", request -> request
                        .filter(jwtHandlerFilter)
                        .GET("", http())
                        .POST("", http())
                        .PATCH("/{id}", http())
                        .DELETE("/{id}", http()))
                .path("/reports", request -> request
                        .filter(jwtHandlerFilter)
                        .POST("/comment", http())
                        .POST("/review", http())
                        .GET("", http()))
                .path("/admin", request -> request
                        .filter(jwtHandlerFilter)
                        .path("/reports", req -> req
                                .PATCH("/{id}/status", http())
                                .GET("", http())
                                .GET("/{id}", http()))
                        .path("/review-comments", req -> req
                                .DELETE("/{id}", http())
                                .GET("", http()))
                        .path("/reviews", req -> req
                                .GET("", http())
                                .DELETE("/{id}", http())))
                .build();
    }
}
