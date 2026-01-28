package com.authservice.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class RotatingJwkManager {

    private final PublicKeyService publicKeyService;

    private final List<RSAKey> keys = new CopyOnWriteArrayList<>();
    
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24 * 5)
    public void rotate() {
        try {
            rotateKeys();
        } catch (Exception e) {
            log.error("Error while rotating keys {}", e.getMessage());
        }
    }

    private void rotateKeys() throws JOSEException {
        RSAKey newKey = new RSAKeyGenerator(2048)
                .keyID(UUID.randomUUID().toString()).generate();
        publicKeyService.savePublicKey(newKey.toPublicJWK());
        keys.add(0, newKey);
        if (keys.size() > 2) {
            keys.remove(keys.size() - 1);
        }
    }

    public RSAKey getActivePrivateKey() {
        return keys.get(0);
    }
}