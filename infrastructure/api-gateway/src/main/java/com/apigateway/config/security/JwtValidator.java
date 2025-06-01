package com.apigateway.config.security;

import com.apigateway.dto.JwtVerificationResult;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtValidator {

    private final RestTemplate restTemplate = new RestTemplate();

    public JwtVerificationResult validateToken(String token, String jwkUrl) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWKSet jwkSet = fetchJwkSet(jwkUrl);
        String kid = signedJWT.getHeader().getKeyID();
        RSAKey rsaKey = (RSAKey) jwkSet.getKeyByKeyId(kid);

        if (rsaKey == null) {
            throw new Exception("Invalid kid");
        }

        JWSVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());

        if (!signedJWT.verify(verifier)) {
            throw new Exception("Invalid signature");
        }

        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();


        return parseJwt(claimsSet);
    }

    public JWKSet fetchJwkSet(String jwkSetUrl) throws Exception {
        String json = restTemplate.getForObject(jwkSetUrl, String.class);;

        if(json != null) {
            return JWKSet.parse(json);
        }

        return new JWKSet();
    }

    private JwtVerificationResult parseJwt(JWTClaimsSet claimsSet) {
        Map<String, Object> claims = claimsSet.getClaims();

        String userId = (String) claims.get("sub");
        String roles = claims.get("roles").toString();

        return new JwtVerificationResult(userId, roles);
    }
}
