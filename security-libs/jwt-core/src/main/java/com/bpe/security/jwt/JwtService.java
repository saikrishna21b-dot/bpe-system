package com.bpe.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public final class JwtService {

	private final JwtTokenConfig config;
	private final SecretKey key;
	private final TokenRevocationStore revocationStore;

	public JwtService(JwtTokenConfig config, TokenRevocationStore revocationStore) {

		this.config = config;
		this.revocationStore = revocationStore;
		this.key = Keys.hmacShaKeyFor(config.secret());
	}

	public void revokeToken(String token) {

		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

		long expiry = claims.getExpiration().getTime();
		revocationStore.revoke(claims.getId(), expiry);
	}

	// ============================
	// TOKEN GENERATION
	// ============================

	public String generateAccessToken(String userId, List<String> roles, String sessionId, int passwordVersion) {

		long now = System.currentTimeMillis();

		return Jwts.builder().setIssuer(config.issuer()).setAudience(config.audience()).setSubject(userId)
				.setId(UUID.randomUUID().toString()).claim(JwtConstants.ROLES, roles)
				.claim(JwtConstants.SESSION_ID, sessionId).claim(JwtConstants.PASSWORD_VERSION, passwordVersion)
				.claim(JwtConstants.TOKEN_TYPE, JwtConstants.ACCESS_TOKEN).setIssuedAt(new Date(now))
				.setExpiration(new Date(now + config.accessTokenTtlMs())).signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	// ============================
	// TOKEN VALIDATION
	// ============================

	public JwtClaims validateAccessToken(String token) {

		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(key).requireIssuer(config.issuer())
					.requireAudience(config.audience()).build().parseClaimsJws(token).getBody();

			JwtValidator.validate(claims, config,revocationStore);

			return new JwtClaims(claims.getSubject(), claims.get(JwtConstants.ROLES, List.class),
					claims.get(JwtConstants.SESSION_ID, String.class),
					claims.get(JwtConstants.PASSWORD_VERSION, Integer.class), claims.getId());

		} catch (ExpiredJwtException e) {
			throw new JwtException(JwtErrorCode.TOKEN_EXPIRED, "JWT token expired");
		} catch (SecurityException e) {
			throw new JwtException(JwtErrorCode.TOKEN_TAMPERED, "JWT signature invalid");
		} catch (JwtException e) {
			throw e;
		} catch (Exception e) {
			throw new JwtException(JwtErrorCode.TOKEN_INVALID, "JWT token invalid");
		}
	}
}
