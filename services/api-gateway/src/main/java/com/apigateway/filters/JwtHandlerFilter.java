package com.apigateway.filters;

import com.apigateway.dto.JwtVerificationResult;
import com.apigateway.dto.response.ErrorResponseDto;
import com.apigateway.exceptions.JwtValidationException;
import com.apigateway.service.JwtValidationService;
import com.nimbusds.jose.JOSEException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    private final static String JWT_SET_URL = "http://auth-service/public-keys";
    private static final ErrorResponseDto invalidJwtResponse = new ErrorResponseDto(
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            HttpStatus.UNAUTHORIZED.value(),
            "Session is not valid"
    );
    private static final ErrorResponseDto errorResponse = new ErrorResponseDto(
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            HttpStatus.UNAUTHORIZED.value(),
            "Invalid JWT format"
    );
    private static final ErrorResponseDto internalErrorResponse = new ErrorResponseDto(
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal server error occurred. Please try again later."
    );

    @Value("${SIGNATURE_SECRET}")
    private String signatureSecret;
    private final JwtValidationService jwtValidator;

    @Override
    public ServerResponse filter(
            @NonNull ServerRequest request,
            @NonNull HandlerFunction<ServerResponse> next
    ) {
        log.info("JWT handler filter");
        String token = extractToken(request);

        if (token == null) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            JwtVerificationResult jwt = jwtValidator.validateToken(token, JWT_SET_URL);

            ServerRequest modified = ServerRequest
                    .from(request)
                    .header("X-User-Id", jwt.userId())
                    .header("X-Roles", jwt.roles())
                    .header("X-Signature", createHmac(jwt.userId() + jwt.roles()))
                    .build();

            return next.handle(modified);
        } catch (JwtValidationException e) {
            log.error("Jwt token is invalid {}", e.getMessage());
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).body(invalidJwtResponse);
        } catch (ParseException | JOSEException e) {
            log.error("Failed to parse token {}", e.getMessage());
            return ServerResponse.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            log.error("Internal filter exception occurred {}", e.getMessage());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(internalErrorResponse);
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
