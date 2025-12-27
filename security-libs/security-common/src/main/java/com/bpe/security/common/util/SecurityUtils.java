package com.bpe.security.common.util;

import com.bpe.security.common.AuthenticatedUser;
import com.bpe.security.common.SecurityContextHolderExt;

/**
 * Utility methods for accessing security context safely.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    public static boolean isAuthenticated() {
        return SecurityContextHolderExt.isAuthenticated();
    }

    public static AuthenticatedUser currentUser() {
        return SecurityContextHolderExt.getUser();
    }

    public static String currentUserId() {
        AuthenticatedUser user = currentUser();
        return user != null ? user.userId() : null;
    }

    public static String currentSessionId() {
        AuthenticatedUser user = currentUser();
        return user != null ? user.sessionId() : null;
    }

    public static boolean hasRole(String role) {
        AuthenticatedUser user = currentUser();
        return user != null && user.hasRole(role);
    }

    public static void requireAuthenticated() {
        if (!isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
    }
}
