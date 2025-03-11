package com.example.ratelimiter.api.dto;

public class RateLimitResponse {
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

    // Static factory methods for common responses
    public static RateLimitResponse allowed(long limit, long remaining, long resetTime) {
        return new RateLimitResponse(true, limit, remaining, resetTime);
    }

    public static RateLimitResponse denied(long limit, long resetTime) {
        return new RateLimitResponse(false, limit, 0, resetTime);
    }

    // Getters and setters
    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getRemaining() {
        return remaining;
    }

    public void setRemaining(long remaining) {
        this.remaining = remaining;
    }

    public long getResetTime() {
        return resetTime;
    }

    public void setResetTime(long resetTime) {
        this.resetTime = resetTime;
    }
}
