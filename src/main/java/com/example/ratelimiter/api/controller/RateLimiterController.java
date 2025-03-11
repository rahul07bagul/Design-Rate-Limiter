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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        try {
            RateLimitResponse response = rateLimiterService.isAllowed(resourceId, clientId);
            if(!response.isAllowed()){
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
            }
            return ResponseEntity.ok(response);
        } catch (RateLimitExceededException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @PostMapping("/rules/reload")
    public ResponseEntity<String> reloadRules() {
        ruleLoaderService.loadRules();
        return ResponseEntity.ok("Rules reloaded successfully");
    }

    @GetMapping("/rules")
    public ResponseEntity<List<Rule>> getAllRules() {
        List<Rule> rules = ruleLoaderService.getAllRules();
        return ResponseEntity.ok(rules);
    }
}
