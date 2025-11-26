package com.authservice.controller;

import com.authservice.services.PublicKeyService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public-keys")
@RequiredArgsConstructor
public class PublicKeyController {
    private final PublicKeyService publicKeyService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getJwk() {
        List<JWK> publicKeys = publicKeyService.getPublicKeys();
        return ResponseEntity.ok(new JWKSet(publicKeys).toJSONObject());
    }
}
