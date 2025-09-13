package com.authservice.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<Object, String> kafkaTemplate;


    public void sendJwkSetInvalidationEvent() {
        log.info("Sending jwk-set-invalidation event");
        kafkaTemplate.send("jwk-set-invalidation", "jwk-set-invalidation");
    }
}
