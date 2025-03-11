package com.example.ratelimiter.core.service;

import com.example.ratelimiter.api.dto.RateLimitResponse;
import com.example.ratelimiter.core.algorithm.RateLimitAlgorithm;
import com.example.ratelimiter.core.storage.RedisRateLimitRepository;
import com.example.ratelimiter.model.Algorithm;
import com.example.ratelimiter.model.Rule;
import com.example.ratelimiter.util.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RateLimiterService {

    private final RuleLoaderService ruleLoaderService;
    private final KeyGenerator keyGenerator;
    private final Map<String, RateLimitAlgorithm> algorithmsMap;
    private final RedisRateLimitRepository redisRateLimitRepository;

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);

    @Autowired
    public RateLimiterService(RuleLoaderService ruleLoaderService, RedisRateLimitRepository redisRateLimitRepository, KeyGenerator keyGenerator, List<RateLimitAlgorithm> algorithms) {
        this.ruleLoaderService = ruleLoaderService;
        this.redisRateLimitRepository = redisRateLimitRepository;
        this.keyGenerator = keyGenerator;
        this.algorithmsMap = algorithms.stream()
                .collect(Collectors.toMap(
                        RateLimitAlgorithm::getAlgorithmType,
                        Function.identity()
                ));
    }

    public RateLimitResponse isAllowed(String resourceId, String clientId){
        Rule rule = ruleLoaderService.getRule(resourceId);
        RateLimitResponse rateLimitResponse;
        long resetTime = Instant.now().getEpochSecond() + rule.getPeriod();

        if(rule == null){
            logger.debug("No rate limit rule found for resource: {}", resourceId);
            rateLimitResponse = new RateLimitResponse(true, 0, 0, resetTime);
            return rateLimitResponse;
        }

        String key = keyGenerator.generateKey(resourceId, clientId);
        RateLimitAlgorithm algorithm = algorithmsMap.get(rule.getAlgorithm().name());

        if(algorithm == null){
            logger.debug("No rate limit algorithm found for algorithm: {}", rule.getAlgorithm().name());
            rateLimitResponse = new RateLimitResponse(true, 0, 0, resetTime);
            return rateLimitResponse;
        }

        long remaining = getRemainingQuota(rule, key);
        boolean allowed = algorithm.isAllowed(key, rule);
        rateLimitResponse = new RateLimitResponse(allowed, rule.getLimit(), remaining, resetTime);
        if (!allowed) {
            logger.info("Rate limit exceeded for resource: {}, client: {}", resourceId, clientId);
        }
        return rateLimitResponse;
    }

    private long getRemainingQuota(Rule rule, String key) {
        if (rule.getAlgorithm() == Algorithm.FIXED_WINDOW_COUNTER) {
            Long currentCount = redisRateLimitRepository.getValue(key);
            return currentCount == null ? rule.getLimit() : Math.max(0, rule.getLimit() - currentCount);
        } else if (rule.getAlgorithm() == Algorithm.TOKEN_BUCKET) {
            String tokenKey = key + ":tokens";
            Long tokens = redisRateLimitRepository.getValue(tokenKey);
            return tokens == null ? rule.getLimit() : tokens;
        } else if (rule.getAlgorithm() == Algorithm.LEAKING_BUCKET) {
            String queueKey = key + ":queue";
            Long queueSize = redisRateLimitRepository.getValue(queueKey);
            return queueSize == null ? rule.getLimit() : Math.max(0, rule.getLimit() - queueSize);
        }

        return 0;
    }
}
