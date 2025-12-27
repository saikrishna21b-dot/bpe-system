package com.bpe.security.jwt;

public interface TokenRevocationStore {

    /**
     * @param tokenId JWT jti
     * @return true if token is revoked
     */
    boolean isRevoked(String tokenId);

    /**
     * Revoke token until its expiry
     */
    void revoke(String tokenId, long expiryEpochMs);
}
