package com.example.ratelimiter.core.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.ratelimiter.core.storage.RedisRateLimitRepository;
import com.example.ratelimiter.model.Algorithm;
import com.example.ratelimiter.model.Rule;

@Component
public class FixedWindowCounterAlgorithm implements RateLimitAlgorithm {

    private final RedisRateLimitRepository redisRepository;

    @Autowired
    public FixedWindowCounterAlgorithm(RedisRateLimitRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public boolean isAllowed(String key, Rule rule) {
        Long currentCount = redisRepository.incrementAndGet(key, 1L);

        // If this is the first request in the window, set the expiry
        if (currentCount == 1) {
            redisRepository.setExpiry(key, rule.getPeriod());
        }

        return currentCount <= rule.getLimit();
    }

    @Override
    public String getAlgorithmType() {
        return Algorithm.FIXED_WINDOW_COUNTER.name();
    }
}
