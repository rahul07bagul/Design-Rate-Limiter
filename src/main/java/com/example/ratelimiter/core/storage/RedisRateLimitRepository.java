package com.example.ratelimiter.core.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisRateLimitRepository implements LimitOperation {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisRateLimitRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Long getValue(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : null;
    }

    @Override
    public String getStringValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Long incrementAndGet(String key, Long incrementBy) {
        return redisTemplate.opsForValue().increment(key, incrementBy);
    }

    @Override
    public void setExpiry(String key, long seconds) {
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }
}