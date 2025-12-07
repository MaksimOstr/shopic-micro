package com.authservice.unit.service;

import com.authservice.entity.PublicKey;
import com.authservice.repositories.PublicKeyRepository;
import com.authservice.services.PublicKeyService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicKeyServiceTest {

    @Mock
    private PublicKeyRepository publicKeyRepository;

    @InjectMocks
    private PublicKeyService publicKeyService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publicKeyService, "expiresAt", 3600);
    }

    @Test
    void savePublicKey_shouldPersistPublicRepresentation() throws Exception {
        RSAKey rsaKey = new RSAKeyGenerator(2048).keyID("kid-1").generate();
        when(publicKeyRepository.save(any(PublicKey.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Instant before = Instant.now();

        publicKeyService.savePublicKey(rsaKey);

        ArgumentCaptor<PublicKey> captor = ArgumentCaptor.forClass(PublicKey.class);
        verify(publicKeyRepository).save(captor.capture());
        PublicKey entity = captor.getValue();
        assertEquals("kid-1", entity.getKeyId());
        assertEquals(rsaKey.toPublicJWK().toString(), entity.getPublicKey());
        assertEquals("RSA", entity.getAlgorithm());
        assertEquals(2048, entity.getKeySize());
        assertTrue(entity.getExpiresAt().isAfter(before));
    }

    @Test
    void getPublicKeys_shouldParseStoredKeys() throws Exception {
        RSAKey rsaKey = new RSAKeyGenerator(2048).keyID("kid-2").generate();
        PublicKey stored = new PublicKey();
        stored.setKeyId("kid-2");
        stored.setPublicKey(rsaKey.toPublicJWK().toString());
        when(publicKeyRepository.findAll()).thenReturn(List.of(stored));

        List<JWK> keys = publicKeyService.getPublicKeys();

        assertEquals(1, keys.size());
        assertEquals("kid-2", keys.getFirst().getKeyID());
    }

    @Test
    void getPublicKeys_shouldThrowWhenJwkParsingFails() {
        PublicKey stored = new PublicKey();
        stored.setPublicKey("invalid");
        when(publicKeyRepository.findAll()).thenReturn(List.of(stored));

        assertThrows(RuntimeException.class, () -> publicKeyService.getPublicKeys());
    }
}
