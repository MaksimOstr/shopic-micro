package com.authservice.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RotatingJwkManager {

    private final PublicKeyService publicKeyService;
    private final KafkaEventProducer authEventProducer;

    private final List<RSAKey> keys = new CopyOnWriteArrayList<>();
    
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 3)
    public void init() {
        try {
            rotateKeys();
            authEventProducer.sendJwkSetInvalidationEvent();
        } catch (Exception e) {
            log.error("Error while rotating keys {}", e.getMessage());
        }
    }

    private void rotateKeys() throws JOSEException {
        RSAKey newKey = new RSAKeyGenerator(2048)
                .keyID(UUID.randomUUID().toString()).generate();
        publicKeyService.savePublicKey(newKey);
        keys.addFirst(newKey);
        if (keys.size() > 2) {
            keys.removeLast();
        }
    }

    public RSAKey getActivePrivateKey() {
        return keys.getFirst();
    }
}