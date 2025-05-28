package com.apigateway.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;


@Component
public class JwtHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final JwtDecoder jwtDecoder;

    public JwtHandlerFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        String token = extractToken(request);
        if (token == null) {
            System.out.println("tetrewrwerwerwer");
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).body("Missing token");
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);
            request.attributes().put("X-User-Id", jwt.getSubject());
            request.attributes().put("X-Roles", jwt.getClaimAsStringList("roles"));
            return next.handle(request);
        } catch (Exception e) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    private String extractToken(ServerRequest request) {
        String authHeader = request.headers().firstHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
