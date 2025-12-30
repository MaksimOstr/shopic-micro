package com.apigateway.config;

import com.apigateway.config.properties.JwtProperties;
import com.apigateway.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] permittedURLs = {
            "/api/v1/auth/**",
            "/api/v1/verification",
            "/auth/oauth2/authorization/google",
            "/auth/login/oauth2/code/google",
            "/favicon.ico",
            "/api/v1/forgot-password",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/api/v1/categories/**",
            "/api/v1/brands/**",
            "/api/v1/products/**",

    };

    private final JwtProperties jwtProperties;


    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint
    ) throws Exception {
        return http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/logout").authenticated()
                        .requestMatchers(permittedURLs).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> {
                            jwtConfigurer.decoder(jwtDecoder());
                        }))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(customAuthenticationEntryPoint))
                .build();
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withJwkSetUri(jwtProperties.getPublicKeyUrl())
                .jwsAlgorithm(SignatureAlgorithm.from(jwtProperties.getHeaderAlg()))
                .build();

        jwtDecoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(jwtProperties.getIssuer())
        );

        return jwtDecoder;
    }
}
