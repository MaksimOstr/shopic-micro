package com.authservice.services;

import com.authservice.entity.PublicKey;
import com.authservice.repositories.PublicKeyRepository;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PublicKeyService {
    private final PublicKeyRepository publicKeyRepository;

    @Value("${PUBLIC_KEY_EXPIRATION}")
    private int expiration;

    public void savePublicKey(RSAKey publicKey) {
        PublicKey key = new PublicKey();
        key.setKeyId(publicKey.getKeyID());
        key.setPublicKey(publicKey.toPublicJWK().toString());
        key.setExpiresAt(getExpireDate());

        publicKeyRepository.save(key);
    }

    public List<JWK> getPublicKeys() {
        return publicKeyRepository.findAll()
                .stream()
                .map(entity -> {
                    try {
                        JWK jwk = JWK.parse(entity.getPublicKey());
                        System.out.println(jwk);
                        if (jwk instanceof RSAKey) {
                            System.out.println("is a jwk test");
                            RSAKey.Builder builder = new RSAKey.Builder((RSAKey) jwk);
                            if (entity.getKeyId() != null) {
                                builder.keyID(entity.getKeyId());
                            }
                            builder.algorithm(Algorithm.parse("RS256"));
                            return builder.build();
                        }

                        return jwk;
                    } catch (ParseException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private Instant getExpireDate() {
        return Instant.now().plusSeconds(expiration);
    }
}
