package com.bpe.security.jwt;

/**
 * Provides current password version for a user.
 * Implementation lives OUTSIDE jwt-core (IAM / cache / Redis).
 */
public interface PasswordVersionProvider {

    int currentVersion(String userId);
}
