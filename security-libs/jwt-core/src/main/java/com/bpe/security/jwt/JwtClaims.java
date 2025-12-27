package com.bpe.security.jwt;

import java.util.List;

public final class JwtClaims {

    private final String userId;
    private final List<String> roles;
    private final String sessionId;
    private final int passwordVersion;
    private final String tokenId;

    public JwtClaims(
            String userId,
            List<String> roles,
            String sessionId,
            int passwordVersion,
            String tokenId) {

        this.userId = userId;
        this.roles = List.copyOf(roles);
        this.sessionId = sessionId;
        this.passwordVersion = passwordVersion;
        this.tokenId = tokenId;
    }

    public String userId() {
        return userId;
    }

    public List<String> roles() {
        return roles;
    }

    public String sessionId() {
        return sessionId;
    }

    public int passwordVersion() {
        return passwordVersion;
    }

    public String tokenId() {
        return tokenId;
    }
}
