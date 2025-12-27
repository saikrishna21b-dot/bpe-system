package com.bpe.security.jwt;

public final class JwtException extends RuntimeException {

    private final JwtErrorCode errorCode;

    public JwtException(JwtErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public JwtErrorCode getErrorCode() {
        return errorCode;
    }
}
