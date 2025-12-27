package com.bpe.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.bpe.security.jwt.JwtClaims;
import com.bpe.security.jwt.JwtService;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String HDR_USER_ID = "X-Auth-UserId";
    private static final String HDR_ROLES = "X-Auth-Roles";
    private static final String HDR_SESSION_ID = "X-Auth-SessionId";

    private final JwtService jwtService;
    private final RedisSessionService sessionService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            RedisSessionService sessionService) {
        this.jwtService = jwtService;
        this.sessionService = sessionService;
    }

    @Override
    public int getOrder() {
        return -1; // run before routing
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String token = extractToken(exchange);
        if (token == null) {
            return chain.filter(exchange); // public endpoint
        }

        return validateToken(token)
                .flatMap(claims -> enforceRedisPolicies(claims, exchange))
                .flatMap(mutated -> chain.filter(mutated))
                .onErrorResume(UnauthorizedException.class,
                        e -> reject(exchange, HttpStatus.UNAUTHORIZED))
                .onErrorResume(ForbiddenException.class,
                        e -> reject(exchange, HttpStatus.FORBIDDEN));
    }

    /* =======================
       Helper methods
       ======================= */

    private String extractToken(ServerWebExchange exchange) {
        String auth = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (auth == null || !auth.startsWith("Bearer ")) {
            return null;
        }
        return auth.substring(7);
    }

    private Mono<JwtClaims> validateToken(String token) {
        return Mono.fromCallable(() -> jwtService.validateAccessToken(token)).onErrorReturn(null);
    }

    private Mono<ServerWebExchange> enforceRedisPolicies(
            JwtClaims claims,
            ServerWebExchange exchange) {

        return sessionService.isUserBlocked(claims.userId())
                .flatMap(blocked -> {
                    if (blocked) {
                        return Mono.error(new ForbiddenException());
                    }
                    return sessionService.isSessionActive(claims.sessionId());
                })
                .flatMap(active -> {
                    if (!active) {
                        return Mono.error(new UnauthorizedException());
                    }
                    return Mono.just(propagateHeaders(exchange, claims));
                });
    }

    private ServerWebExchange propagateHeaders(
            ServerWebExchange exchange,
            JwtClaims claims) {

        return exchange.mutate()
                .request(req -> req.headers(headers -> {
                    headers.add(HDR_USER_ID, claims.userId());
                    headers.add(HDR_ROLES, String.join(",", claims.roles()));
                    headers.add(HDR_SESSION_ID, claims.sessionId());
                }))
                .build();
    }

    private Mono<Void> reject(ServerWebExchange exchange,
                              HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    /* =======================
       Internal marker exceptions
       ======================= */

    private static class UnauthorizedException extends RuntimeException {}
    private static class ForbiddenException extends RuntimeException {}
}
