package com.example.ratelimiter.api.dto;

public class RateLimitRequest {
    private String resourceId;
    private String clientId;

    public RateLimitRequest() {}

    public RateLimitRequest(String resourceId, String clientId) {
        this.resourceId = resourceId;
        this.clientId = clientId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
