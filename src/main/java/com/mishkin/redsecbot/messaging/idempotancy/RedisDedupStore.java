package com.mishkin.redsecbot.messaging.idempotancy;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author a.mishkin
 */
@Component
public class RedisDedupStore implements DedupStore {

    private static final Duration TTL = Duration.ofHours(6);

    private final StringRedisTemplate redis;

    public RedisDedupStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public boolean claim(String correlationId) {
        /*
        true → ключ был создан сейчас
        false → ключ уже существовал
        null → Redis не ответил / ошибка
         */
        Boolean success = redis.opsForValue()
                .setIfAbsent(key(correlationId), "1", TTL);

        return Boolean.TRUE.equals(success);
    }

    private String key(String correlationId) {
        return "redsecbot:stats-enriched:done:" + correlationId;
    }
}

