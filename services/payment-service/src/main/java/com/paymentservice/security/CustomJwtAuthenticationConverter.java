package com.paymentservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(jwt.getClaim("role")));

        CustomPrincipal principal = new CustomPrincipal(
                UUID.fromString(jwt.getSubject())
        );

        return new JwtAuthenticationToken(jwt, authorities, null) {
            @Override
            public Object getPrincipal() {
                return principal;
            }
        };
    }
}
