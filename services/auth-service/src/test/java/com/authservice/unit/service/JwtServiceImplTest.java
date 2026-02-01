package com.authservice.unit.service;

import com.authservice.config.properties.JwtProperties;
import com.authservice.entity.UserRolesEnum;
import com.authservice.services.impl.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {
    @Mock
    private JwtEncoder jwtEncoder;

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setExpiresAt(3600);
        properties.setIssuer("auth-service");
        jwtService = new JwtServiceImpl(properties, jwtEncoder);
    }

    @Test
    void generateToken_shouldBuildClaimsAndUseEncoder() {
        Instant now = Instant.now();
        Jwt jwt = new Jwt(
                "generated-token",
                now,
                now.plusSeconds(3600),
                Map.of("alg", "RS256"),
                Map.of("role", UserRolesEnum.ROLE_USER.name())
        );
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        String token = jwtService.generateToken("123", UserRolesEnum.ROLE_USER);

        assertEquals("generated-token", token);
    }
}
