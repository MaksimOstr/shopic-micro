package com.apigateway.service;

import com.apigateway.dto.JwtVerificationResult;
import com.apigateway.exceptions.JwtValidationException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtValidationService {
    @Lazy
    @Autowired
    private JwtValidationService jwtValidator;
    private final RestTemplate restTemplate = new RestTemplate();


    public JwtVerificationResult validateToken(String token, String jwkUrl) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWKSet jwkSet = jwtValidator.fetchJwkSet(jwkUrl);
        String kid = signedJWT.getHeader().getKeyID();
        RSAKey rsaKey = (RSAKey) jwkSet.getKeyByKeyId(kid);

        if (rsaKey == null) {
            throw new JwtValidationException("Invalid signature");
        }

        JWSVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());

        if (!signedJWT.verify(verifier)) {
            throw new JwtValidationException("Invalid signature");
        }

        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        return parseJwt(claimsSet);
    }

    @Cacheable("jwk-sets")
    public JWKSet fetchJwkSet(String jwkSetUrl) throws ParseException {
        String json = restTemplate.getForObject(jwkSetUrl, String.class);;

        if(json != null) {
            return JWKSet.parse(json);
        }

        return new JWKSet();
    }

    @CacheEvict(value = "jwk-sets", allEntries = true)
    public void evictJwkSetsCache() {}


    private JwtVerificationResult parseJwt(JWTClaimsSet claimsSet) {
        Map<String, Object> claims = claimsSet.getClaims();

        String userId = (String) claims.get("sub");
        String roles = claims.get("roles").toString();

        return new JwtVerificationResult(userId, roles);
    }

}
