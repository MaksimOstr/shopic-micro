package com.authservice.services;

import com.authservice.config.properties.JwtProperties;
import com.authservice.entity.UserRolesEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;


@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties properties;
    private final JwtEncoder jwtEncoder;

    public String generateToken(String subject, UserRolesEnum userRole) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(properties.getExpiresAt());
        JwsHeader jwsHeader = JwsHeader.with(properties::getHeaderAlg).build();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(subject)
                .issuer(properties.getIssuer())
                .claim("role", userRole.name())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
    }
}
