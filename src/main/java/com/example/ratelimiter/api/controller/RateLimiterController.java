package com.example.ratelimiter.api.controller;

import com.example.ratelimiter.api.dto.RateLimitRequest;
import com.example.ratelimiter.api.dto.RateLimitResponse;
import com.example.ratelimiter.core.service.RateLimiterService;
import com.example.ratelimiter.core.service.RuleLoaderService;
import com.example.ratelimiter.exception.RateLimitExceededException;
import com.example.ratelimiter.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/rate-limit")
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;
    private final RuleLoaderService ruleLoaderService;

    @Autowired
    public RateLimiterController(RateLimiterService rateLimiterService, RuleLoaderService ruleLoaderService) {
        this.rateLimiterService = rateLimiterService;
        this.ruleLoaderService = ruleLoaderService;
    }

    @PostMapping("/check")
    public ResponseEntity<RateLimitResponse> checkRateLimit(@RequestBody RateLimitRequest request) {
        String resourceId = request.getResourceId();
        String clientId = request.getClientId();

        Rule rule = ruleLoaderService.getRule(resourceId);

        // If no rule exists, allow the request
        if (rule == null) {
            return ResponseEntity.ok(RateLimitResponse.allowed(Long.MAX_VALUE, Long.MAX_VALUE, 0));
        }

        try {
            boolean allowed = rateLimiterService.isAllowed(resourceId, clientId);

            // Calculate reset time (approximate for token bucket and leaking bucket)
            long resetTime = Instant.now().getEpochSecond() + rule.getPeriod();

            // For simplicity, we're not calculating exact remaining tokens
            // This would require additional storage logic
            long remaining = allowed ? 1 : 0;

            RateLimitResponse response = allowed
                    ? RateLimitResponse.allowed(rule.getLimit(), remaining, resetTime)
                    : RateLimitResponse.denied(rule.getLimit(), resetTime);

            return ResponseEntity.ok(response);
        } catch (RateLimitExceededException e) {
            // Calculate reset time
            long resetTime = Instant.now().getEpochSecond() + rule.getPeriod();

            RateLimitResponse response = RateLimitResponse.denied(rule.getLimit(), resetTime);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
        }
    }

    @PostMapping("/rules/reload")
    public ResponseEntity<String> reloadRules() {
        ruleLoaderService.loadRules();
        return ResponseEntity.ok("Rules reloaded successfully");
    }
}
