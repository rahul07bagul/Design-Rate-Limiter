package com.example.ratelimiter.core.algorithm;

import com.example.ratelimiter.model.Rule;

public interface RateLimitAlgorithm {
    boolean isAllowed(String key, Rule rule);
    String getAlgorithmType();
}
