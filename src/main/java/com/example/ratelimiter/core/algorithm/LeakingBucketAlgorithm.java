package com.example.ratelimiter.core.algorithm;

import com.example.ratelimiter.core.storage.RedisRateLimitRepository;
import com.example.ratelimiter.model.Algorithm;
import com.example.ratelimiter.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class LeakingBucketAlgorithm  implements RateLimitAlgorithm{

    private final RedisRateLimitRepository redisRateLimitRepository;

    @Autowired
    public LeakingBucketAlgorithm(RedisRateLimitRepository redisRateLimitRepository) {
        this.redisRateLimitRepository = redisRateLimitRepository;
    }

    @Override
    public boolean isAllowed(String key, Rule rule) {
        String queueKey = key + ":queue";
        String lastLeakKey = key + ":lastLeak";

        Long queueSize = redisRateLimitRepository.getValue(queueKey);
        String lastLeakSize = redisRateLimitRepository.getStringValue(lastLeakKey);

        long now = Instant.now().getEpochSecond();
        long lastLeak = lastLeakSize != null ? Long.parseLong(lastLeakSize) : now;

        long leadPeriod = rule.getPeriod();
        long capacity = rule.getLimit();

        double leadRate = (double) capacity / leadPeriod;

        long elapsedTime = now - lastLeak;
        long leakedTokens  = (long) (elapsedTime * leadRate);

        long currentQueueSize = queueSize != null ? queueSize : 0;
        long newQueueSize = Math.max(0, currentQueueSize - leakedTokens);

        // If the bucket is full, reject the request
        if(newQueueSize >= capacity) {
            return false;
        }

        newQueueSize += 1; //new request

        /*
        * Example:
        * Limit = 60;
        * Period = 60
        * 60 requests in 60 seconds
        *
        * CurrentQueueSize = 4
        * leadRate = 60/60 = 1
        * leakedTokens = 2 * 1 = 2
        *
        * newQueueSize = 4 - 2 = 2;
        * newQueueSize = 2 + 1 = 3
        *
        * */

        redisRateLimitRepository.setValue(queueKey, String.valueOf(newQueueSize));
        redisRateLimitRepository.setValue(lastLeakKey, String.valueOf(now));

        redisRateLimitRepository.setExpiry(queueKey, rule.getPeriod() * 2);
        redisRateLimitRepository.setExpiry(lastLeakKey, rule.getPeriod() * 2);

        return true;
    }

    @Override
    public String getAlgorithmType() {
        return Algorithm.LEAKING_BUCKET.name();
    }
}
