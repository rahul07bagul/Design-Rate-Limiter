package com.example.ratelimiter.core.algorithm;

import com.example.ratelimiter.core.storage.RedisRateLimitRepository;
import com.example.ratelimiter.model.Algorithm;
import com.example.ratelimiter.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeakingBucketAlgorithm  implements RateLimitAlgorithm{

    private final RedisRateLimitRepository redisRateLimitRepository;

    @Autowired
    public LeakingBucketAlgorithm(RedisRateLimitRepository redisRateLimitRepository) {
        this.redisRateLimitRepository = redisRateLimitRepository;
    }

    @Override
    public boolean isAllowed(String key, Rule rule) {
        return false;
    }

    @Override
    public String getAlgorithmType() {
        return Algorithm.LEAKING_BUCKET.name();
    }
}
