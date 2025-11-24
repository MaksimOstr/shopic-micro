package com.apigateway.service;

import com.apigateway.dto.JwtVerificationResult;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwkService {
    private final RestTemplate restTemplate;


    @Cacheable("jwk-sets")
    public JWKSet fetchJwkSet(String jwkSetUrl) throws ParseException {
        String json = restTemplate.getForObject(jwkSetUrl, String.class);;
        log.info("Fetching JWK Set from {}", jwkSetUrl);
        if(json != null) {
            return JWKSet.parse(json);
        }

        return new JWKSet();
    }

    @CacheEvict(value = "jwk-sets", allEntries = true)
    public void evictJwkSetsCache() {}


    public JwtVerificationResult parseJwt(JWTClaimsSet claimsSet) {
        Map<String, Object> claims = claimsSet.getClaims();

        String userId = (String) claims.get("sub");
        String roles = claims.get("roles").toString();

        System.out.println(roles);

        return new JwtVerificationResult(userId, roles);
    }
}
