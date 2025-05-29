package com.authservice.config.security;

import com.authservice.services.RotatingJwkManager;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.*;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final RotatingJwkManager manager;


    @Bean
    JwtEncoder jwtEncoder() {
        JWKSource<SecurityContext> jwkSource = (jwkSelector, context) -> List.of(manager.getActivePrivateKey());
        return new NimbusJwtEncoder(jwkSource);
    }
}
