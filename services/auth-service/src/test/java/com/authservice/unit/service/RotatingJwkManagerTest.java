package com.authservice.unit.service;

import com.authservice.services.KafkaEventProducer;
import com.authservice.services.PublicKeyService;
import com.authservice.services.RotatingJwkManager;
import com.nimbusds.jose.jwk.RSAKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RotatingJwkManagerTest {

    @Mock
    private PublicKeyService publicKeyService;

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @InjectMocks
    private RotatingJwkManager rotatingJwkManager;


    @Test
    void rotate_shouldGenerateKeyPersistAndPublishEvent() {
        rotatingJwkManager.rotate();

        ArgumentCaptor<RSAKey> captor = ArgumentCaptor.forClass(RSAKey.class);
        verify(publicKeyService).savePublicKey(captor.capture());
        verify(kafkaEventProducer).sendJwkSetInvalidationEvent();

        RSAKey savedKey = captor.getValue();
        assertNotNull(savedKey.getKeyID());
        assertEquals(savedKey.getKeyID(), rotatingJwkManager.getActivePrivateKey().getKeyID());
    }

    @Test
    void rotate_shouldKeepOnlyTwoLatestKeys() {
        rotatingJwkManager.rotate();
        rotatingJwkManager.rotate();
        rotatingJwkManager.rotate();

        ArgumentCaptor<RSAKey> captor = ArgumentCaptor.forClass(RSAKey.class);
        verify(publicKeyService, times(3)).savePublicKey(captor.capture());
        verify(kafkaEventProducer, times(3)).sendJwkSetInvalidationEvent();

        @SuppressWarnings("unchecked")
        List<RSAKey> keys = (List<RSAKey>) ReflectionTestUtils.getField(rotatingJwkManager, "keys");
        assertNotNull(keys);
        assertEquals(2, keys.size());
        List<RSAKey> generated = captor.getAllValues();
        assertEquals(generated.get(2).getKeyID(), rotatingJwkManager.getActivePrivateKey().getKeyID());
        assertEquals(generated.get(1).getKeyID(), keys.get(keys.size() - 1).getKeyID());
    }
}
