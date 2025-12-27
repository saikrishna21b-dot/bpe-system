package com.bpe.security.common;

import java.util.List;
import java.util.Objects;

/**
 * Represents the authenticated user for the current request.
 * Immutable and thread-safe.
 */
public final class AuthenticatedUser {

    private final String userId;
    private final List<String> roles;
    private final String sessionId;

    public AuthenticatedUser(
            String userId,
            List<String> roles,
            String sessionId) {

        this.userId = Objects.requireNonNull(userId, "userId");
        this.roles = List.copyOf(roles);
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
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

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
