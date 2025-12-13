package com.apigateway.filters;

import com.apigateway.dto.ErrorResponseDto;
import com.apigateway.exceptions.JwtValidationException;
import com.nimbusds.jose.JOSEException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import java.text.ParseException;

import static com.apigateway.utils.CryptoUtils.createHmac;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

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
    private final JwtDecoder jwtDecoder;

    @Override
    public ServerResponse filter(
            @NonNull ServerRequest request,
            @NonNull HandlerFunction<ServerResponse> next
    ) throws Exception {
        log.info("JWT handler filter");
        String token = extractToken(request);

        if (token == null) {
            return next.handle(request);
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);
            String subject = jwt.getSubject();
            String role = jwt.getClaimAsString("role");

            ServerRequest modified = ServerRequest
                    .from(request)
                    .header("X-User-Id", subject)
                    .header("X-Role", role)
                    .header("X-Signature", createHmac(subject + role, signatureSecret))
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
}
