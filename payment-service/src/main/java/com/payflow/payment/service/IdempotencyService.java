package com.payflow.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflow.common.exception.IdempotencyConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private static final Duration DEFAULT_TTL = Duration.ofHours(24);
    private static final String KEY_PREFIX = "idempotency:";
    private static final String LOCK_VALUE = "PROCESSING";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public Optional<String> getCachedResponse(String idempotencyKey) {
        String key = KEY_PREFIX + idempotencyKey;
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return Optional.empty();
        }

        if (LOCK_VALUE.equals(value)) {
            throw new IdempotencyConflictException(idempotencyKey);
        }
        log.debug("Idempotency cache hit for key: {}", idempotencyKey);
        return Optional.of(value);
    }

    public boolean acquireLock(String idempotencyKey) {
        String key = KEY_PREFIX + idempotencyKey;
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, LOCK_VALUE, DEFAULT_TTL);
        if (Boolean.TRUE.equals(acquired)) {
            log.debug("Idempotency lock acquired for key: {}", idempotencyKey);
            return true;
        }

        return false;
    }

    public <T> void cacheResponse(String idempotencyKey, T response) {
        String key = KEY_PREFIX + idempotencyKey;
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(key, json, DEFAULT_TTL);
            log.debug("Idempotency response cached for key: {}", idempotencyKey);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize response for idempotency key: {}", idempotencyKey, e);
            redisTemplate.delete(key);
        }
    }

    public void releaseLock(String idempotencyKey) {
        String key = KEY_PREFIX + idempotencyKey;
        redisTemplate.delete(key);
        log.debug("Idempotency lock released for key: {}", idempotencyKey);
    }

    public <T> T deserialize(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize cached response", e);
            throw new RuntimeException("Failed to deserialize cached response", e);
        }
    }





}
