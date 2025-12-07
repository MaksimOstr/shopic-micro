package com.authservice.controller;

import com.authservice.services.PublicKeyService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public-keys")
@RequiredArgsConstructor
@Tag(name = "Public Keys", description = "Provides the current JSON Web Keys (JWKs) for access token validation")
public class PublicKeyController {
    private final PublicKeyService publicKeyService;

    @Operation(
            summary = "List active JWKs",
            description = "Returns the JSON Web Key Set (JWKS) used to verify JWTs issued by the auth-service."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "JWKS returned.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    description = "Standard JWKS payload",
                                    example = """
                                            {
                                              "keys": [
                                                {
                                                  "kty": "RSA",
                                                  "kid": "6e9f2a4c-2c6a-4dab-9fc0-2f6c2375ec1e",
                                                  "use": "sig",
                                                  "alg": "RS256",
                                                  "n": "....",
                                                  "e": "AQAB"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getJwk() {
        List<JWK> publicKeys = publicKeyService.getPublicKeys();
        return ResponseEntity.ok(new JWKSet(publicKeys).toJSONObject());
    }
}
