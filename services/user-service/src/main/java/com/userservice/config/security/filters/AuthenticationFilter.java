package com.userservice.config.security.filters;

import com.userservice.dto.CustomPrincipal;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    @Value("${SIGNATURE_SECRET}")
    private String signatureSecret;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");
        String roles = request.getHeader("X-Roles");
        String signature = request.getHeader("X-Signature");
        System.out.println(roles);
        System.out.println(userId);
        System.out.println(userId + " " + roles + " " + signature);
        System.out.println(userId + " " + roles + " " + signature);
        if(!verifyHmac(roles, userId, signature)) {
            response.sendError(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase()
            );
        }

        CustomPrincipal principal = new CustomPrincipal(userId);
        Authentication authToken = new UsernamePasswordAuthenticationToken(principal, null, toSimpleGrantedAuthorities(roles));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        filterChain.doFilter(request, response);
    }


    private boolean verifyHmac(
            @NonNull String roles,
            @NonNull String userId,
            @NonNull String signature) {
        String data = userId + roles;
        String generatedSignature = createHmac(data);

        return generatedSignature.equals(signature);
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

    private List<SimpleGrantedAuthority> toSimpleGrantedAuthorities(String roles) {
        String cleaned = roles.replaceAll("^\\[|]$", "");
        System.out.println(cleaned);
        List<String> roleList = Arrays.asList(cleaned.split(","));

        return roleList.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
