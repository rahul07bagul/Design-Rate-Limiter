package com.example.ratelimiter.core.service;

import com.example.ratelimiter.core.algorithm.RateLimitAlgorithm;
import com.example.ratelimiter.exception.RateLimitExceededException;
import com.example.ratelimiter.model.Rule;
import com.example.ratelimiter.util.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RateLimiterService {

    private final RuleLoaderService ruleLoaderService;
    private final KeyGenerator keyGenerator;
    private final Map<String, RateLimitAlgorithm> algorithmsMap;

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);

    @Autowired
    public RateLimiterService(RuleLoaderService ruleLoaderService, KeyGenerator keyGenerator, List<RateLimitAlgorithm> algorithms) {
        this.ruleLoaderService = ruleLoaderService;
        this.keyGenerator = keyGenerator;
        this.algorithmsMap = algorithms.stream()
                .collect(Collectors.toMap(
                        RateLimitAlgorithm::getAlgorithmType,
                        Function.identity()
                ));
    }

    public boolean isAllowed(String resourceId, String clientId){
        Rule rule = ruleLoaderService.getRule(resourceId);

        if(rule == null){
            logger.debug("No rate limit rule found for resource: {}", resourceId);
            return true;
        }

        String key = keyGenerator.generateKey(resourceId, clientId);

        RateLimitAlgorithm algorithm = algorithmsMap.get(rule.getAlgorithm().name());

        if(algorithm == null){
            logger.debug("No rate limit algorithm found for algorithm: {}", rule.getAlgorithm().name());
            return true;
        }

        boolean allowed = algorithm.isAllowed(key, rule);
        if (!allowed) {
            logger.info("Rate limit exceeded for resource: {}, client: {}", resourceId, clientId);
        }

        return allowed;
    }

    public void checkRateLimit(String resourceId, String clientId) throws RateLimitExceededException {
        if(!isAllowed(resourceId, clientId)){
            throw new RateLimitExceededException("Rate limit exceeded for resources");
        }
    }
}
