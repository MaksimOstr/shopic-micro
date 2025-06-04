package com.apigateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private final JwtValidationService jwtValidationService;

    @KafkaListener(topics = "jwk-set-invalidation", groupId = "api-gateway")
    public void invalidateJwkSetCache(Acknowledgment acknowledgment) {
        log.info("Received jwk-set-invalidation event");
        jwtValidationService.evictJwkSetsCache();

        acknowledgment.acknowledge();
    }
}
