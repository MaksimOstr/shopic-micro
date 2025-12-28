package com.paymentservice.security;

import java.util.UUID;

public record CustomPrincipal(
        UUID id
) {}
