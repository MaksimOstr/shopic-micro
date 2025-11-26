package com.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.authservice.utils.CryptoUtils.createHmac;

@Component
@Slf4j
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

        if(userId == null || roles == null || signature == null){
            filterChain.doFilter(request, response);
            return;
        }

        if (!verifyHmac(roles, userId, signature)) {
            response.sendError(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase()
            );

            return;
        }

        setSecurityContext(userId, roles);

        filterChain.doFilter(request, response);
    }


    private boolean verifyHmac(
            @NonNull String roles,
            @NonNull String userId,
            @NonNull String signature
    ) {
        String data = userId + roles;
        String generatedSignature = createHmac(data, signatureSecret);

        return generatedSignature.equals(signature);
    }

    private List<SimpleGrantedAuthority> toSimpleGrantedAuthorities(String roles) {
        String cleaned = roles.replaceAll("^\\[|]$", "");
        List<String> roleList = Arrays.asList(cleaned.split(","));

        return roleList.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private void setSecurityContext(String userId, String roles) {
        CustomPrincipal principal = new CustomPrincipal(userId);
        Authentication authToken = new UsernamePasswordAuthenticationToken(principal, null, toSimpleGrantedAuthorities(roles));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

}
