package com.bpe.security.common;

/**
 * Holds security context for the current request thread.
 * MUST be cleared after request completion.
 */
public final class SecurityContextHolderExt {

    private static final ThreadLocal<AuthenticatedUser> CONTEXT =
            new ThreadLocal<>();

    private SecurityContextHolderExt() {}

    public static void setUser(AuthenticatedUser user) {
        CONTEXT.set(user);
    }

    public static AuthenticatedUser getUser() {
        return CONTEXT.get();
    }

    public static boolean isAuthenticated() {
        return CONTEXT.get() != null;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
