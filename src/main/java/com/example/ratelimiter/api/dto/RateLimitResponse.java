package com.example.ratelimiter.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RateLimitResponse {
    // Getters and setters
    private boolean allowed;
    private long limit;
    private long remaining;
    private long resetTime;

    // Default constructor
    public RateLimitResponse() {
    }

    public RateLimitResponse(boolean allowed, long limit, long remaining, long resetTime) {
        this.allowed = allowed;
        this.limit = limit;
        this.remaining = remaining;
        this.resetTime = resetTime;
    }
}
