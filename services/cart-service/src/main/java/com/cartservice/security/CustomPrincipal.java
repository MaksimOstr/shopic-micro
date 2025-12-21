package com.cartservice.security;


import java.util.UUID;

public record CustomPrincipal(
        UUID id
) {
}

