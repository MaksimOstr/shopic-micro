package com.productservice.config;

import com.productservice.config.properties.JwtProperties;
import com.productservice.security.CustomAuthenticationEntryPoint;
import com.productservice.security.CustomJwtAuthenticationConverter;
import com.shopic.grpc.productservice.ProductServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.security.AuthenticationProcessInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProperties jwtProperties;

    private static final String[] permittedURLs = {
            "/actuator/**",
            "/api/v1/categories/**",
            "/api/v1/products/**",
            "/api/v1/brands/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api-docs/**"
    };


    @Bean
    AuthenticationProcessInterceptor grpcFilterChain(GrpcSecurity security) throws Exception {
        return security.authorizeRequests(auth -> auth
                        .allRequests().permitAll()
                )
                .build();

    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomJwtAuthenticationConverter customJwtAuthenticationConverter
    ) throws Exception {
        return http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(permittedURLs).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> {
                            jwtConfigurer
                                    .jwtAuthenticationConverter(customJwtAuthenticationConverter)
                                    .decoder(jwtDecoder());
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
