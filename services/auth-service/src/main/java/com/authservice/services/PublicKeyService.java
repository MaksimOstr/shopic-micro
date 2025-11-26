package com.authservice.services;

import com.authservice.config.properties.JwtProperties;
import com.authservice.entity.PublicKey;
import com.authservice.repositories.PublicKeyRepository;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class PublicKeyService {
    private final PublicKeyRepository publicKeyRepository;

    private final JwtProperties jwtProperties;

    @Value("${public-key.expires-at}")
    private int expiresAt;

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

                        if (jwk instanceof RSAKey) {
                            RSAKey.Builder builder = new RSAKey.Builder((RSAKey) jwk);
                            if (entity.getKeyId() != null) {
                                builder.keyID(entity.getKeyId());
                            }
                            builder.algorithm(Algorithm.parse(jwtProperties.getHeaderAlg()));
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
        return Instant.now().plusSeconds(expiresAt);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60)
    private void deleteExpiredKeys() {
        log.info("Deleting expired public keys");
        publicKeyRepository.deleteExpiredKeys();
    }
}
