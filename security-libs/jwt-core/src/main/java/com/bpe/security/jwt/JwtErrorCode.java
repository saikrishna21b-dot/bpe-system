package com.bpe.security.jwt;

public enum JwtErrorCode {
    TOKEN_EXPIRED,
    TOKEN_INVALID,
    TOKEN_TAMPERED,
    TOKEN_UNSUPPORTED,
    TOKEN_WRONG_TYPE,
    TOKEN_ISSUER_INVALID,
    TOKEN_AUDIENCE_INVALID
}
