package com.apigateway.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Value("${JWT_ISSUER}")
    private String jwtIssuer;


    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri("http://localhost:56205/auth/jwk-set").jwsAlgorithm(SignatureAlgorithm.RS256).build();
        JwtIssuerValidator issuerValidator = new JwtIssuerValidator(jwtIssuer);

        jwtDecoder.setJwtValidator(issuerValidator);

        return jwtDecoder;
    }

}
