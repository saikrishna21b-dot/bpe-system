package com.bpe.gateway.security;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class RedisSessionService {

    private final ReactiveStringRedisTemplate redis;

    public RedisSessionService(ReactiveStringRedisTemplate redis) {
        this.redis = redis;
    }

    public Mono<Boolean> isSessionActive(String sessionId) {
        return redis.hasKey("session:" + sessionId);
    }

    public Mono<Boolean> isUserBlocked(String userId) {
        return redis.hasKey("user:block:" + userId);
    }
}
