package com.example.ratelimiter.core.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.ratelimiter.core.storage.RedisRateLimitRepository;
import com.example.ratelimiter.core.storage.LimitOperation;
import com.example.ratelimiter.model.Algorithm;
import com.example.ratelimiter.model.Rule;

import java.time.Instant;

@Component
public class TokenBucketAlgorithm implements RateLimitAlgorithm {

    private final RedisRateLimitRepository redisRepository;

    @Autowired
    public TokenBucketAlgorithm(RedisRateLimitRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public boolean isAllowed(String key, Rule rule) {
        String tokenKey = key + ":tokens";
        String lastRefillKey = key + ":lastRefill";

        Long tokens = redisRepository.getValue(tokenKey);
        String lastRefillStr = redisRepository.getStringValue(lastRefillKey);

        long now = Instant.now().getEpochSecond();
        long lastRefill = lastRefillStr != null ? Long.parseLong(lastRefillStr) : 0;

        long refillTime = rule.getPeriod();
        long refillAmount = rule.getLimit();

        // Token refill logic
        long newTokens;
        if (tokens == null) {
            newTokens = refillAmount - 1; // Subtract 1 for the current request
            redisRepository.setValue(lastRefillKey, String.valueOf(now));
        } else {
            long elapsedTime = now - lastRefill;
            long tokensToAdd = (elapsedTime * refillAmount) / refillTime;

            if (tokensToAdd > 0) {
                newTokens = Math.min(refillAmount, tokens + tokensToAdd) - 1;
                redisRepository.setValue(lastRefillKey, String.valueOf(now));
            } else {
                newTokens = tokens - 1;
            }
        }

        if (newTokens < 0) {
            return false;
        }

        redisRepository.setValue(tokenKey, String.valueOf(newTokens));

        redisRepository.setExpiry(tokenKey, rule.getPeriod());
        redisRepository.setExpiry(lastRefillKey, rule.getPeriod());

        return true;
    }

    @Override
    public String getAlgorithmType() {
        return Algorithm.TOKEN_BUCKET.name();
    }
}
