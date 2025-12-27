package com.bpe.security.jwt;

import io.jsonwebtoken.Claims;

import java.util.List;

final class JwtValidator {

	private JwtValidator() {
	}

	static void validate(Claims claims, JwtTokenConfig config, TokenRevocationStore revocationStore) {

		if (revocationStore.isRevoked(claims.getId())) {
			throw new JwtException(JwtErrorCode.TOKEN_INVALID, "Token has been revoked");
		}

		if (!config.issuer().equals(claims.getIssuer())) {
			throw new JwtException(JwtErrorCode.TOKEN_ISSUER_INVALID, "Invalid JWT issuer");
		}

		if (!config.audience().equals(claims.getAudience())) {
			throw new JwtException(JwtErrorCode.TOKEN_AUDIENCE_INVALID, "Invalid JWT audience");
		}

		if (!JwtConstants.ACCESS_TOKEN.equals(claims.get(JwtConstants.TOKEN_TYPE))) {
			throw new JwtException(JwtErrorCode.TOKEN_WRONG_TYPE, "Invalid token type");
		}

		if (!(claims.get(JwtConstants.ROLES) instanceof List)) {
			throw new JwtException(JwtErrorCode.TOKEN_INVALID, "Invalid roles claim");
		}

		if (claims.get(JwtConstants.SESSION_ID) == null) {
			throw new JwtException(JwtErrorCode.TOKEN_INVALID, "Missing session id");
		}

		if (claims.get(JwtConstants.PASSWORD_VERSION) == null) {
			throw new JwtException(JwtErrorCode.TOKEN_INVALID, "Missing password version");
		}
	}
}
