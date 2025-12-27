package com.bpe.security.jwt;

import java.util.Arrays;

public final class JwtTokenConfig {

    private final String issuer;
    private final String audience;
    private final long accessTokenTtlMs;
    private final byte[] secret;

    public JwtTokenConfig(
            String issuer,
            String audience,
            long accessTokenTtlMs,
            byte[] secret) {

        if (secret == null || secret.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 256 bits (32 bytes)");
        }

        this.issuer = issuer;
        this.audience = audience;
        this.accessTokenTtlMs = accessTokenTtlMs;
        this.secret = Arrays.copyOf(secret, secret.length);
    }

    public String issuer() {
        return issuer;
    }

    public String audience() {
        return audience;
    }

    public long accessTokenTtlMs() {
        return accessTokenTtlMs;
    }

    public byte[] secret() {
        return Arrays.copyOf(secret, secret.length);
    }
}
