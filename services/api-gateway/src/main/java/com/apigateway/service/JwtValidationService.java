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
import org.springframework.stereotype.Service;
import java.text.ParseException;


@Service
@RequiredArgsConstructor
public class JwtValidationService {

    private final JwkService jwkService;

    public JwtVerificationResult validateToken(String token, String jwkUrl) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWKSet jwkSet = jwkService.fetchJwkSet(jwkUrl);
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

        return jwkService.parseJwt(claimsSet);
    }


}
