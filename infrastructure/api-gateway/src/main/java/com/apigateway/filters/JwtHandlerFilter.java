package com.apigateway.filters;

import com.apigateway.dto.response.ErrorResponseDto;
import jakarta.servlet.ServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final JwtDecoder jwtDecoder;

    private static final ErrorResponseDto errorResponse = new ErrorResponseDto(
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            HttpStatus.UNAUTHORIZED.value(),
            "Session is not valid"
    );

    @Value("${SIGNATURE_SECRET}")
    private String signatureSecret;

    @Override
    public ServerResponse filter(
            @NonNull ServerRequest request,
            @NonNull HandlerFunction<ServerResponse> next
    ) {
        String token = extractToken(request);
        if (token == null) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);
            String userId = jwt.getSubject();
            String roles = jwt.getClaimAsStringList("roles").toString();
            String userRoles = String.join(",", roles);

            ServerRequest modified = ServerRequest
                    .from(request)
                    .header("X-User-Id", userId)
                    .header("X-Roles", userRoles)
                    .header("X-Signature", createHmac(userId + userRoles))
                    .build();

            return next.handle(modified);
        } catch (Exception e) {
            log.error("Failed to decode JWT token {}", e.getMessage());
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    private String extractToken(ServerRequest request) {
        String authHeader = request.headers().firstHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private String createHmac(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(signatureSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC", e);
        }
    }
}
