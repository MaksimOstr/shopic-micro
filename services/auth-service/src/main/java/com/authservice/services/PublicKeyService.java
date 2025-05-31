package com.authservice.services;

import com.authservice.entity.PublicKey;
import com.authservice.repositories.PublicKeyRepository;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

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

    private Instant getExpireDate() {
        return Instant.now().plusSeconds(expiration);
    }
}
