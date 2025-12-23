package com.productservice.security;


import java.util.UUID;

public record CustomPrincipal(
        UUID id
) {}
